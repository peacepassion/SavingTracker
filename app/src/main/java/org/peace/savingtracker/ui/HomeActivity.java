package org.peace.savingtracker.ui;

import android.os.Bundle;
import autodagger.AutoInjector;
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
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_home;
  }
}
