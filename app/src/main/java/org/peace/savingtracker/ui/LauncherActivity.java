package org.peace.savingtracker.ui;

import android.content.Intent;
import android.os.Bundle;
import com.orhanobut.hawk.Hawk;
import org.peace.savingtracker.consts.HawkKeys;

public class LauncherActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Hawk.get(HawkKeys.IS_LOGGED, false)) {
      startActivity(new Intent(this, HomeActivity.class));
    } else {
      startActivity(new Intent(this, LoginActivity.class));
    }
    startSplashActivity();
    finish();
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
