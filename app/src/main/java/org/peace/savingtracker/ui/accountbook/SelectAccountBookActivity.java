package org.peace.savingtracker.ui.accountbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import autodagger.AutoInjector;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.joanzapata.iconify.widget.IconTextView;
import de.greenrobot.event.EventBus;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.UserManager;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/23.
 */
@AutoInjector(MyApp.class) public class SelectAccountBookActivity extends BaseActivity {

  @Inject AVCloudAPI api;

  @Bind(R.id.list) RecyclerView bookRV;

  private BookAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    setupBookListView();
    requestAccountBooks();
  }

  private void setupBookListView() {
    bookRV.setLayoutManager(new LinearLayoutManager(this));
    adapter = new BookAdapter(this);
    bookRV.setAdapter(adapter);
  }

  private void requestAccountBooks() {
    ProgressDialog progressDialog = new ProgressDialog(this);
    api.queryAll(AccountBook.class)
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

    private LayoutInflater inflater;
    private List<AccountBook> books;

    public BookAdapter(BaseActivity activity) {
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
      holder.rootView.setOnClickListener(v -> {
        userManager.setCurrentAccountBook(book);
        notifyDataSetChanged();
      });
      AccountBook currentBook = userManager.getCurrentBook();
      if (currentBook == null) {
        holder.selectIndicatorTV.setVisibility(View.GONE);
        return;
      }
      if (currentBook.getObjectId().equals(book.getObjectId())) {
        holder.selectIndicatorTV.setVisibility(View.VISIBLE);
        holder.rootView.setOnClickListener(null);
      } else  {
        holder.selectIndicatorTV.setVisibility(View.GONE);
      }
    }

    @Override public int getItemCount() {
      return books.size();
    }
  }
}
