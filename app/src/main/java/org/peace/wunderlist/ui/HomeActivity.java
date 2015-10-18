package org.peace.wunderlist.ui;

import android.os.Bundle;
import org.peace.wunderlist.R;

/**
 * Created by peacepassion on 15/10/14.
 */
public class HomeActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.home_activity));
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_home;
  }
}