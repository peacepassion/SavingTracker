package org.peace.savingtracker.ui.accountbook;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import autodagger.AutoInjector;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVQuery;
import com.joanzapata.iconify.widget.IconTextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.model.FriendManager;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;
import org.peace.savingtracker.utils.ResUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/23.
 */
@AutoInjector(MyApp.class) public class SelectAccountBookActivity extends BaseActivity {

  @Inject FriendManager friendManager;

  @Bind(R.id.list) RecyclerView listView;

  private BookAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    setTitle(getString(R.string.select_account_book));

    setupBookListView();
    requestAccountBooks();
  }

  private void setupBookListView() {
    listView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new BookAdapter(this);
    listView.setAdapter(adapter);
  }

  private void requestAccountBooks() {
    ProgressDialog progressDialog = new ProgressDialog(this);

    Observable<List<AccountBook>> ownedBooks =
        cloudAPI.queryIs(AccountBook.class, AccountBook.OWNER, userManager.getCurrentUser());

    AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
    query.whereContainsAll(AccountBook.SHARED_USERS, Arrays.asList(userManager.getCurrentUser()));
    Observable<List<AccountBook>> sharedBooks = cloudAPI.query(query);

    Observable.concat(ownedBooks, sharedBooks)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<AccountBook>>() {
          @Override public void onStart() {
            progressDialog.show();
          }

          @Override public void onCompleted() {
            progressDialog.dismiss();
          }

          @Override public void onError(Throwable e) {
            progressDialog.dismiss();
          }

          @Override public void onNext(List<AccountBook> accountBooks) {
            adapter.setBooks(accountBooks);
          }
        });
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_select_account_book;
  }

  static class BookVH extends RecyclerView.ViewHolder {

    @Bind(R.id.root) ViewGroup rootView;
    @Bind(R.id.name) TextView bookNameTV;
    @Bind(R.id.selected_indicator) IconTextView selectIndicatorTV;

    public BookVH(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  @AutoInjector(MyApp.class) public static class BookAdapter extends RecyclerView.Adapter<BookVH> {

    @Inject UserManager userManager;

    private SelectAccountBookActivity activity;
    private LayoutInflater inflater;
    private List<AccountBook> books;

    public BookAdapter(SelectAccountBookActivity activity) {
      this.activity = activity;
      activity.getAppComponent().inject(this);
      books = new LinkedList<>();
      inflater = LayoutInflater.from(activity);
    }

    public void setBooks(List<AccountBook> books) {
      this.books = books;
      notifyDataSetChanged();
    }

    @Override public BookVH onCreateViewHolder(ViewGroup parent, int viewType) {
      return new BookVH(inflater.inflate(R.layout.item_select_account_book, parent, false));
    }

    @Override public void onBindViewHolder(BookVH holder, int position) {
      AccountBook book = books.get(position);
      holder.bookNameTV.setText(book.getName());

      AccountBook currentBook = userManager.getCurrentBook();
      holder.rootView.setOnLongClickListener(v -> {
        new MaterialDialog.Builder(v.getContext()).items(new CharSequence[] {
            ResUtil.getString(R.string.select_account_book),
            ResUtil.getString(R.string.share_account_book)
        }).itemsCallback((dialog, itemView, which, text) -> {
          if (which == 0) {
            userManager.setCurrentAccountBook(book);
            notifyDataSetChanged();
          } else if (which == 1) {
            shareAccountBook(currentBook);
          }
        }).build().show();

        return true;
      });

      if (currentBook == null) {
        holder.selectIndicatorTV.setVisibility(View.GONE);
        return;
      }
      if (currentBook.getObjectId().equals(book.getObjectId())) {
        holder.selectIndicatorTV.setVisibility(View.VISIBLE);
        holder.rootView.setOnClickListener(null);
      } else {
        holder.selectIndicatorTV.setVisibility(View.GONE);
      }
    }

    private void shareAccountBook(AccountBook accountBook) {
      final List<User> friends = new LinkedList<>();
      activity.friendManager.getFriends().subscribeOn(Schedulers.io()).map((List<User> users) -> {
        friends.addAll(users);
        List<CharSequence> userIds = new LinkedList<>();
        for (User user : users) {
          userIds.add(user.getObjectId());
        }
        return userIds.toArray(new CharSequence[userIds.size()]);
      }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<CharSequence[]>() {
        ProgressDialog dlg = new ProgressDialog(activity);

        @Override public void onStart() {
          dlg.show();
        }

        @Override public void onCompleted() {
          dlg.dismiss();
        }

        @Override public void onError(Throwable e) {
          dlg.dismiss();
          activity.popHint(e.getMessage());
        }

        @Override public void onNext(CharSequence[] userIds) {
          new MaterialDialog.Builder(activity).items(userIds)
              .itemsCallback((dialog, itemView, which, text) -> shareAccountBook(accountBook,
                  friends.get(which)))
              .build()
              .show();
        }
      });
    }

    private void shareAccountBook(AccountBook accountBook, User to) {
      accountBook.addSharedUser(to);
      activity.cloudAPI.update(accountBook)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<Void>() {
            ProgressDialog dlg = new ProgressDialog(activity);

            @Override public void onStart() {
              dlg.show();
            }

            @Override public void onCompleted() {
              dlg.dismiss();
              activity.popHint(activity.getString(R.string.share_succ));
            }

            @Override public void onError(Throwable e) {
              dlg.dismiss();
              activity.popHint(e.getMessage());
            }

            @Override public void onNext(Void aVoid) {

            }
          });
    }

    @Override public int getItemCount() {
      return books.size();
    }
  }
}
