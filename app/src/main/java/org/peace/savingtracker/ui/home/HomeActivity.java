package org.peace.savingtracker.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.OnClick;
import org.peace.savingtracker.BuildConfig;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.AddExpenseActivity;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.history.ExpenseHistoryActivity;
import org.peace.savingtracker.ui.user.UserActivity;

/**
 * Created by peacepassion on 15/10/14.
 */
public class HomeActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.app_name));
    attachDebugDrawer();
  }

  @OnClick({ R.id.add_expense, R.id.view_expense_history, R.id.user_center })
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.add_expense:
        startActivity(new Intent(this, AddExpenseActivity.class));
        break;
      case R.id.view_expense_history:
        startActivity(new Intent(this, ExpenseHistoryActivity.class));
        break;
      case R.id.user_center:
        startActivity(new Intent(this, UserActivity.class));
        break;
      default:
        break;
    }
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
