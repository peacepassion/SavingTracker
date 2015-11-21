package org.peace.savingtracker.ui.user;

import android.app.Activity;
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
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/11.
 */
@AutoInjector(MyApp.class) public class UserActivity extends BaseActivity {

  @Inject AVCloudAPI avCloudAPI;

  @Bind(R.id.user_id) TextView userIdTV;
  @Bind(R.id.username) TextView userNameTV;
  @Bind(R.id.list) RecyclerView accountBookList;

  AccountBookAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    initUI();
  }

  private void initUI() {
    userIdTV.setText("user_id: " + userManager.getCurrentUser().getId());
    userNameTV.setText("username: " + userManager.getCurrentUser().getUsername());
    initAccountBookList();
  }

  private void initAccountBookList() {
    accountBookList.setLayoutManager(new LinearLayoutManager(this));
    adapter = new AccountBookAdapter(this);
    accountBookList.setAdapter(adapter);
    avCloudAPI.query(AccountBook.class, AccountBook.OWNER, userManager.getCurrentUser().getId())
        .compose(bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<AccountBook>>() {
          ProgressDialog dlg = new ProgressDialog(UserActivity.this);

          @Override public void onStart() {
            dlg.show();
          }

          @Override public void onCompleted() {
            dlg.dismiss();
          }

          @Override public void onError(Throwable e) {
            popHint(e.getMessage());
            dlg.dismiss();
          }

          @Override public void onNext(List<AccountBook> accountBooks) {
            adapter.setBooks(accountBooks);
          }
        });
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_user;
  }

  @Override protected boolean needLogin() {
    return true;
  }

  static class AccountBookVH extends RecyclerView.ViewHolder {

    @Bind(R.id.id) TextView idTV;
    @Bind(R.id.name) TextView nameTV;
    @Bind(R.id.description) TextView desTV;
    @Bind(R.id.owner) TextView ownerTV;

    public AccountBookVH(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  static class AccountBookAdapter extends RecyclerView.Adapter<AccountBookVH> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AccountBook> books;

    AccountBookAdapter(Activity activity) {
      this.activity = activity;
      inflater = LayoutInflater.from(activity);
      books = new LinkedList<>();
    }

    public void setBooks(List<AccountBook> books) {
      this.books = books;
      notifyDataSetChanged();
    }

    @Override public AccountBookVH onCreateViewHolder(ViewGroup parent, int viewType) {
      return new AccountBookVH(inflater.inflate(R.layout.item_account_book, parent, false));
    }

    @Override public void onBindViewHolder(AccountBookVH holder, int position) {
      AccountBook book = books.get(position);
      holder.idTV.setText(book.getObjectId());
      holder.nameTV.setText(book.getName());
      holder.desTV.setText(book.getDescription());
      holder.ownerTV.setText(book.getOwner());
    }

    @Override public int getItemCount() {
      return books.size();
    }
  }
}
