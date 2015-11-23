package org.peace.savingtracker.ui;

import android.content.Intent;
import android.os.Bundle;
import com.orhanobut.hawk.Hawk;
import org.peace.savingtracker.consts.HawkKeys;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.home.HomeActivity;
import org.peace.savingtracker.ui.login.LoginActivity;
import org.peace.savingtracker.user.User;

public class LauncherActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (userManager.getCurrentUser() != null) {
      startActivity(new Intent(this, HomeActivity.class));
    } else {
      startActivity(new Intent(this, LoginActivity.class));
    }
    startSplashActivity();
  }

  private void startSplashActivity() {
    Intent intent = new Intent(this, SplashActivity.class);
    startActivity(intent);
  }

  @Override protected int getLayoutRes() {
    return 0;
  }

  @Override protected boolean hasTitle() {
    return false;
  }
}
