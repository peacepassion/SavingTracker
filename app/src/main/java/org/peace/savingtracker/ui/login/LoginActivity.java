package org.peace.savingtracker.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.home.HomeActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;

/**
 * Created by peacepassion on 15/10/14.
 */
public class LoginActivity extends BaseActivity {

  @Bind(R.id.login_username_input) EditText usernameET;
  @Bind(R.id.login_password_input) EditText passwordET;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("登录");
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_login;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == SignUpActivity.SIGN_UP_REQUEST_CODE || resultCode == RESULT_OK) {
      finish();
    }
  }

  @OnClick({ R.id.login, R.id.sign_up }) public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.login:
        if (!validate()) {
          return;
        }
        doLogin();
        break;
      case R.id.sign_up:
        startActivityForResult(new Intent(this, SignUpActivity.class),
            SignUpActivity.SIGN_UP_REQUEST_CODE);
        break;
    }
  }

  private void doLogin() {
    ProgressDialog dlg = new ProgressDialog(this, "正在登录...");
    dlg.show();
    AVUser.logInInBackground(usernameET.getText().toString(), passwordET.getText().toString(),
        new LogInCallback<AVUser>() {
          @Override public void done(AVUser avUser, AVException e) {
            dlg.dismiss();
            if (avUser == null) {
              popHint(e.getMessage(), true);
              return;
            }
            User user = new User(avUser);
            userManager.login(user);
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
          }
        });
  }

  private boolean validate() {
    if (TextUtils.isEmpty(usernameET.getText())) {
      popHint("Username cannot be empty");
      return false;
    }
    if (TextUtils.isEmpty(passwordET.getText())) {
      popHint("Password cannot be empty");
      return false;
    }
    return true;
  }
}
