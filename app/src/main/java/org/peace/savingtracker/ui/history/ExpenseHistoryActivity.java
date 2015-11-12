package org.peace.savingtracker.ui.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import autodagger.AutoInjector;
import butterknife.Bind;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.ExpenseAPI;
import org.peace.savingtracker.ui.base.BaseActivity;

/**
 * Created by peacepassion on 15/11/10.
 */
@AutoInjector(MyApp.class) public class ExpenseHistoryActivity extends BaseActivity {

  @Inject ExpenseAPI expenseAPI;

  @Bind(R.id.expense_history_list) RecyclerView historyRV;

  HistoryAdapter adapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    initHistoryRV();
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  private void initHistoryRV() {
    adapter = new HistoryAdapter(this, expenseAPI);
    historyRV.setLayoutManager(new LinearLayoutManager(this));
    historyRV.setAdapter(adapter);
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_expense_history;
  }
}
