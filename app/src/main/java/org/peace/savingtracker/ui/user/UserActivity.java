package org.peace.savingtracker.ui.user;

import android.app.Activity;
import android.content.Intent;
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
import butterknife.OnClick;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.login.LoginActivity;
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
    accountBookList.setLayoutManager(new LinearLayoutManager(this));
    adapter = new AccountBookAdapter(this);
    accountBookList.setAdapter(adapter);
    requestUserInfo();
  }

  private void requestUserInfo() {
    userManager.syncCurrentUser()
        .flatMap(user -> avCloudAPI.queryIs(AccountBook.class, AccountBook.OWNER,
            userManager.getCurrentUser()))
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
            userIdTV.setText("user_id: " + userManager.getCurrentUser().getObjectId());
            userNameTV.setText("username: " + userManager.getCurrentUser().getUsername());
            adapter.setBooks(accountBooks);
          }
        });
  }

  @OnClick(R.id.logout) public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.logout) {
      userManager.logout();
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
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
      holder.ownerTV.setText(book.getOwner().getUsername());
    }

    @Override public int getItemCount() {
      return books.size();
    }
  }
}
