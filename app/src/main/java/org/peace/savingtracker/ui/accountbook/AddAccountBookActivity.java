package org.peace.savingtracker.ui.accountbook;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import autodagger.AutoInjector;
import butterknife.Bind;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/19.
 */
@AutoInjector(MyApp.class) public class AddAccountBookActivity extends BaseActivity {

  @Inject AVCloudAPI aVCloudAPI;

  @Bind(R.id.name) EditText accountBookNameET;
  @Bind(R.id.description) EditText accountBookDesET;
  @Bind(R.id.confirm) Button confirmBtn;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    setTitle(getString(R.string.add_account));
    initConfirmBtn();
  }

  private void initConfirmBtn() {
    RxView.clickEvents(confirmBtn).compose(bindToLifecycle()).filter(viewClickEvent -> {
      if (TextUtils.isEmpty(accountBookNameET.getText().toString())) {
        popHint("Account book name cannot be empty");
        return false;
      }
      return true;
    }).filter(viewClickEvent -> {
      if (TextUtils.isEmpty(accountBookDesET.getText().toString())) {
        popHint("Account book description cannot be empty");
        return false;
      }
      return true;
    }).subscribe(viewClickEvent -> {
      AccountBook book = new AccountBook();
      book.setName(accountBookNameET.getEditableText().toString());
      book.setDescription(accountBookDesET.getEditableText().toString());
      book.setOwner(userManager.getCurrentUser().getId());
      aVCloudAPI.insert(book)
          .compose(bindToLifecycle())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Subscriber<Void>() {
            ProgressDialog dlg = new ProgressDialog(AddAccountBookActivity.this);

            @Override public void onStart() {
              dlg.show();
            }

            @Override public void onCompleted() {
              dlg.dismiss();
              popHint("Add account book successfully");
            }

            @Override public void onError(Throwable e) {
              dlg.dismiss();
              popHint(e.getMessage());
            }

            @Override public void onNext(Void aVoid) {
              finish();
            }
          });
    });
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_add_account_book;
  }

  @Override protected boolean needLogin() {
    return true;
  }
}
