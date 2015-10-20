package org.peace.savingtracker.ui;

import android.os.Bundle;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/10/14.
 */
public class LoginActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("登录");
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_login;
  }

}
