package org.peace.savingtracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.OnClick;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.AddExpenseActivity;
import org.peace.savingtracker.utils.ResUtil;

/**
 * Created by peacepassion on 15/12/19.
 */
public class HomeExpenseFragment extends BaseHomeFragment {

  public static HomeExpenseFragment newInstance() {
    return new HomeExpenseFragment();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_home_expense;
  }

  @Override public String getTitle() {
    return ResUtil.getString(R.string.add_expense);
  }

  @OnClick(R.id.add_expense) public void gotoAddExpense() {
    startActivity(new Intent(activity, AddExpenseActivity.class));
  }

  @OnClick(R.id.add_income) public void gotoAddIncome() {
    // todo
    //startActivity(new Intent());
  }
}
