package org.peace.savingtracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import com.jakewharton.rxbinding.view.RxView;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.ui.accountbook.SelectAccountBookActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;
import org.peace.savingtracker.utils.SystemUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/12/19.
 */
public class HomeExpenseFragment extends BaseHomeFragment {

  public static HomeExpenseFragment newIntance() {
    return new HomeExpenseFragment();
  }

  @Bind(R.id.expense_amount_et) EditText expenseAmountET;
  @Bind(R.id.expense_category_sp) Spinner expenseCategorySp;
  @Bind(R.id.confirm) Button confirm;
  @Bind(R.id.account_book) TextView accountBookTV;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.add_expense);
    initCategorySpinner();
    initConfirmButton();
    initExpenseAmountET();
    setAccountBookTV();
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_home_expense;
  }

  private void initCategorySpinner() {
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.category,
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
      Expense obj = new Expense(user.getObjectId(), user.getUsername(), System.currentTimeMillis(),
          (String) expenseCategorySp.getSelectedItem(), value,
          userManager.getCurrentBook().getObjectId());
      ProgressDialog dlg = new ProgressDialog(activity);
      cloudAPI.insert(obj)
          .subscribeOn(Schedulers.io())
          .subscribeOn(AndroidSchedulers.mainThread())
          .compose(bindToLifecycle())
          .subscribe(new Subscriber<Void>() {
            @Override public void onStart() {
              dlg.show();
            }

            @Override public void onCompleted() {
              dlg.dismiss();
              popHint("Add expense successfully");
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

  private void setAccountBookTV() {
    AccountBook accountBook = userManager.getCurrentBook();
    if (accountBook == null) {
      accountBookTV.setText("Please select one account book.");
    } else {
      accountBookTV.setText(
          getResources().getString(R.string.selected_account_book, accountBook.getName()));
    }
    accountBookTV.setOnClickListener(
        v -> startActivity(new Intent(activity, SelectAccountBookActivity.class)));
  }

  private void initExpenseAmountET() {
    RxView.focusChangeEvents(expenseAmountET).subscribe(viewFocusChangeEvent -> {
      if (!viewFocusChangeEvent.hasFocus()) {
        SystemUtil.hideKeyboard(expenseAmountET);
      }
    });
  }

  public void onEvent(UserManager.CurrentAccountBookChangeEvent event) {
    setAccountBookTV();
  }

  private class InputValidator {
    boolean validate() {
      return validateAmount();
    }

    private boolean validateAmount() {
      if (TextUtils.isEmpty(expenseAmountET.getText())) {
        popHint("Expense amount cannot be empty.");
        return false;
      }
      String content = expenseAmountET.getText().toString();
      try {
        Float.valueOf(content);
      } catch (Exception e) {
        popHint("Expense amount must be a valid number.");
        return false;
      }
      if (userManager.getCurrentBook() == null) {
        popHint("You have to select target account book.");
        return false;
      }
      return true;
    }
  }
}
