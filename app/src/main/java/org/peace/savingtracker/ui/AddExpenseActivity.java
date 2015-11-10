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
import com.jakewharton.rxbinding.view.ViewClickEvent;
import io.realm.Realm;
import java.util.Date;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseDAO;
import org.peace.savingtracker.model.ExpenseRealmDAO;
import rx.functions.Action1;
import rx.functions.Func1;

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
      float value = 0;
      try {
        value = Float.valueOf(content);
      } catch (Exception e) {
        Snackbar.make(getWindow().getDecorView(), "Expense amount must be a valid number.",
            Snackbar.LENGTH_SHORT).show();
        return false;
      }
      return true;
    }
  }
}
