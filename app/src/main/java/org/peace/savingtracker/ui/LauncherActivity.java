package org.peace.savingtracker.ui;

import android.content.Intent;
import android.os.Bundle;
import autodagger.AutoInjector;
import com.orhanobut.hawk.Hawk;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.consts.HawkKeys;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.home.HomeActivity;
import org.peace.savingtracker.ui.login.LoginActivity;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;

@AutoInjector(MyApp.class)
public class LauncherActivity extends BaseActivity {

  @Inject UserManager userManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    User user = Hawk.get(HawkKeys.CURRENT_USER, null);
    if (user != null) {
      userManager.login(user);
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
