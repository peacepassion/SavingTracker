package org.peace.savingtracker.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.Bind;
import com.jakewharton.rxbinding.view.RxView;
import io.realm.Realm;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseDAO;
import org.peace.savingtracker.model.ExpenseRealmDAO;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.utils.SystemUtil;

/**
 * Created by peacepassion on 15/11/10.
 */
public class AddExpenseActivity extends BaseActivity {

  @Bind(R.id.expense_amount_et) EditText expenseAmountET;
  @Bind(R.id.expense_category_sp) Spinner expenseCategorySp;
  @Bind(R.id.confirm) Button confirm;

  Realm realm;
  ExpenseDAO expenseDAO;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initDAO();
    initCategorySpinner();
    initConfirmButton();
    initExpenseAmountET();
  }

  @Override protected void onDestroy() {
    realm.close();
    super.onDestroy();
  }

  private void initDAO() {
    realm = Realm.getDefaultInstance();
    expenseDAO = new ExpenseRealmDAO(realm);
  }

  private void initCategorySpinner() {
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category,
        android.R.layout.simple_spinner_dropdown_item);
    expenseCategorySp.setAdapter(adapter);
  }

  private void initConfirmButton() {
    RxView.clickEvents(confirm).filter(viewClickEvent -> {
      InputValidator validator = new InputValidator();
      return validator.validate();
    }).subscribe(viewClickEvent -> {
      double value = Double.valueOf(expenseAmountET.getText().toString());
      Expense obj = new Expense(expenseDAO.getMaxId() + 1, "test-user", "test-uer",
          System.currentTimeMillis(), (String) expenseCategorySp.getSelectedItem(), value);
      expenseDAO.insert(obj);
      finish();
    });
  }

  private void initExpenseAmountET() {
    RxView.focusChangeEvents(expenseAmountET).subscribe(viewFocusChangeEvent -> {
      if (!viewFocusChangeEvent.hasFocus()) {
        SystemUtil.hideKeyboard(expenseAmountET);
      }
    });
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_add_expense;
  }

  private class InputValidator {
    boolean validate() {
      return validateAmount();
    }

    private boolean validateAmount() {
      if (TextUtils.isEmpty(expenseAmountET.getText())) {
        Snackbar.make(getWindow().getDecorView(), "Expense amount cannot be empty.",
            Snackbar.LENGTH_SHORT).show();
        return false;
      }
      String content = expenseAmountET.getText().toString();
      try {
        Float.valueOf(content);
      } catch (Exception e) {
        Snackbar snackbar = Snackbar.make(expenseAmountET, "Expense amount must be a valid number.",
            Snackbar.LENGTH_SHORT);
        snackbar.show();
        return false;
      }
      return true;
    }
  }
}
