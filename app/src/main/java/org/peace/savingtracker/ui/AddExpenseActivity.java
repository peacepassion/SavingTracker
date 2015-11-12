package org.peace.savingtracker.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import autodagger.AutoInjector;
import butterknife.Bind;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseAPI;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.utils.SystemUtil;

/**
 * Created by peacepassion on 15/11/10.
 */
@AutoInjector(MyApp.class) public class AddExpenseActivity extends BaseActivity {

  @Inject ExpenseAPI expenseAPI;

  @Bind(R.id.expense_amount_et) EditText expenseAmountET;
  @Bind(R.id.expense_category_sp) Spinner expenseCategorySp;
  @Bind(R.id.confirm) Button confirm;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    initCategorySpinner();
    initConfirmButton();
    initExpenseAmountET();
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
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
      User user = userManager.getCurrentUser();
      Expense obj = new Expense(user.getId(), user.getUsername(), System.currentTimeMillis(),
          (String) expenseCategorySp.getSelectedItem(), value);
      ProgressDialog dlg = new ProgressDialog(this);
      dlg.show();
      expenseAPI.insert(obj, new SaveCallback() {
        @Override public void done(AVException e) {
          dlg.dismiss();
          if (e != null) {
            popHint(e.getMessage());
            return;
          }
          finish();
        }
      });
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
