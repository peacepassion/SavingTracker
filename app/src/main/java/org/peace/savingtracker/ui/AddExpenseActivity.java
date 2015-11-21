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
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.utils.SystemUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/10.
 */
@AutoInjector(MyApp.class) public class AddExpenseActivity extends BaseActivity {

  @Inject AVCloudAPI AVCloudAPI;

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
      AVCloudAPI.insert(obj)
          .subscribeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .compose(bindToLifecycle())
          .subscribe(new Subscriber<Void>() {
            @Override public void onStart() {
              dlg.show();
            }

            @Override public void onCompleted() {
              dlg.dismiss();
              finish();
            }

            @Override public void onError(Throwable e) {
              dlg.dismiss();
              popHint(e.getMessage());
            }

            @Override public void onNext(Void aVoid) {

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
