package org.peace.savingtracker.ui;

import android.app.Activity;
import android.os.Bundle;
import autodagger.AutoInjector;
import org.peace.savingtracker.BuildConfig;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/10/14.
 */
@AutoInjector({ MyApp.class }) public class HomeActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    setTitle(getString(R.string.app_name));
    attachDebugDrawer();
  }

  private void attachDebugDrawer() {
    if (BuildConfig.DEBUG) {
      try {
        Class.forName("org.peace.savingtracker.ui.UIUtil")
            .getMethod("attachDebugView", Activity.class)
            .invoke(null, this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_home;
  }

  @Override protected boolean allowActionUp() {
    return false;
  }
}
