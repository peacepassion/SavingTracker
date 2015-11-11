package org.peace.savingtracker.ui.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import io.realm.Realm;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.ExpenseDAO;
import org.peace.savingtracker.model.ExpenseRealmDAO;
import org.peace.savingtracker.ui.base.BaseActivity;

/**
 * Created by peacepassion on 15/11/10.
 */
public class ExpenseHistoryActivity extends BaseActivity {

  @Bind(R.id.expense_history_list) RecyclerView historyRV;

  HistoryAdapter adapter;

  Realm realm;
  ExpenseDAO expenseDAO;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initDAO();
    initHistoryRV();
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected void onDestroy() {
    realm.close();
    super.onDestroy();
  }

  private void initDAO() {
    realm = Realm.getDefaultInstance();
    expenseDAO = new ExpenseRealmDAO(realm);
  }

  private void initHistoryRV() {
    adapter = new HistoryAdapter(this, expenseDAO);
    historyRV.setLayoutManager(new LinearLayoutManager(this));
    historyRV.setAdapter(adapter);
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_expense_history;
  }
}
