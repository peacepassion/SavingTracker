package org.peace.savingtracker.ui;

import android.os.Bundle;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/10/14.
 */
public class SplashActivity extends BaseActivity {

  private static final int DURATION = 2000;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(0, 0);
    findViewById(android.R.id.content).postDelayed(() -> {
      finish();
      overridePendingTransition(0, 0);
    }, DURATION);
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_splash;
  }

  @Override protected boolean hasTitle() {
    return false;
  }

  // disable back operation on this page
  @Override public void onBackPressed() {

  }
}
