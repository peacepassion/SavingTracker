package org.peace.savingtracker.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.home.HomeActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.utils.ResUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/12.
 */
public class SignUpActivity extends BaseActivity {

  public static final int SIGN_UP_REQUEST_CODE = 0;

  @Bind(R.id.email) EditText emailET;
  @Bind(R.id.password) EditText passwordET;
  @Bind(R.id.password_confirm) EditText passwordConfirmET;
  @Bind(R.id.username) EditText usernameET;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_sign_up;
  }

  @OnClick({ R.id.sign_up }) public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.sign_up:
        if (!validate()) {
          return;
        }
        doSignUp();
        break;
      default:
        break;
    }
  }

  private void doSignUp() {
    User user = new User();
    user.setUsername(usernameET.getText().toString());
    user.setEmail(emailET.getText().toString());
    user.setPassword(passwordET.getText().toString());

    Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        try {
          user.signUp();
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(null);
            subscriber.onCompleted();
          }
        } catch (AVException e) {
          subscriber.onError(e);
        }
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Void>() {
          ProgressDialog dlg = new ProgressDialog(SignUpActivity.this, "Signing up...");

          @Override public void onStart() {
            dlg.show();
          }

          @Override public void onCompleted() {
            dlg.dismiss();
          }

          @Override public void onError(Throwable e) {
            dlg.dismiss();
            AVException ae = (AVException) e;
            switch (ae.getCode()) {
              case 202:
                popHint(ResUtil.getString(R.string.error_register_user_name_repeat), true);
                break;
              case 203:
                popHint(ResUtil.getString(R.string.error_register_email_repeat), true);
                break;
              default:
                popHint(ResUtil.getString(R.string.network_error), true);
                break;
            }
          }

          @Override public void onNext(Void aVoid) {
            user.changeCurrentUser(user, true);
            popHint(ResUtil.getString(R.string.register_success));
            setResult(RESULT_OK);
            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
            finish();
          }
        });
  }

  private boolean validate() {
    if (TextUtils.isEmpty(emailET.getText())) {
      popHint("Email cannot be empty");
      return false;
    }
    if (TextUtils.isEmpty(passwordET.getText())) {
      popHint("Password cannot be empty");
      return false;
    }
    if (TextUtils.isEmpty(passwordConfirmET.getText())) {
      popHint("Confirm password cannot be empty");
      return false;
    }
    if (TextUtils.isEmpty(usernameET.getText())) {
      popHint("Name cannot be empty");
      return false;
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()) {
      popHint("Email is illegal");
      return false;
    }
    if (!TextUtils.equals(passwordET.getText(), passwordConfirmET.getText())) {
      popHint("Confirmed password must be equals password");
      return false;
    }
    return true;
  }
}
