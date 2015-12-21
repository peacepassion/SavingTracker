package org.peace.savingtracker.ui.history;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import autodagger.AutoInjector;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseAPI;
import org.peace.savingtracker.model.ExpenseHelper;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.UserManager;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/10.
 */
@AutoInjector(MyApp.class) public class HistoryAdapter
    extends RecyclerView.Adapter<HistoryViewHolder> {

  @Inject UserManager userManager;
  @Inject AVCloudAPI cloudAPI;
  @Inject ExpenseAPI expenseAPI;

  private BaseActivity activity;
  private List<Expense> expenses;
  private ProgressDialog progressDialog;

  public HistoryAdapter(BaseActivity activity) {
    this.activity = activity;
    activity.getAppComponent().inject(this);
    progressDialog = new ProgressDialog(activity);
    expenses = Collections.emptyList();
    loadData();
  }

  public void updateDataSet() {
    loadData();
  }

  @Override public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(activity);
    View root = inflater.inflate(R.layout.item_expense_history, parent, false);
    return new HistoryViewHolder(root);
  }

  @Override public void onBindViewHolder(HistoryViewHolder holder, int position) {
    Expense expense = expenses.get(position);
    holder.userName.setText(expense.getName());
    holder.date.setText(ExpenseHelper.getMonthDay(expense));
    holder.category.setText(expense.getCategory());
    holder.amount.setText(String.valueOf(expense.getValue()));
    RxView.clickEvents(holder.edit).subscribe(viewClickEvent -> {
      Snackbar.make(viewClickEvent.view(), "not ready yet", Snackbar.LENGTH_SHORT).show();
    });
    RxView.clickEvents(holder.delete).subscribe(viewClickEvent -> {
      alertDelete(expense);
    });
  }

  private void alertDelete(Expense expense) {
    new MaterialDialog.Builder(activity).title("确定删除")
        .content("确定删除该笔账单?")
        .positiveText("确定")
        .negativeText("取消")
        .callback(new MaterialDialog.ButtonCallback() {
          @Override public void onPositive(MaterialDialog dialog) {
            cloudAPI.delete(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(activity.bindToLifecycle())
                .subscribe(new Subscriber<Void>() {
                  @Override public void onStart() {
                    progressDialog.show();
                  }

                  @Override public void onCompleted() {
                    progressDialog.dismiss();
                  }

                  @Override public void onError(Throwable e) {
                    progressDialog.dismiss();
                    activity.popHint(e.getMessage());
                  }

                  @Override public void onNext(Void aVoid) {
                    expenses.remove(expense);
                    notifyDataSetChanged();
                  }
                });
          }
        })
        .show();
  }

  @Override public int getItemCount() {
    return expenses.size();
  }

  private void loadData() {
    expenseAPI.getCurrentAccountBookExpenses()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(activity.bindToLifecycle())
        .subscribe(new Subscriber<List<Expense>>() {
          @Override public void onStart() {
            progressDialog.show();
          }

          @Override public void onCompleted() {
            progressDialog.dismiss();
          }

          @Override public void onError(Throwable e) {
            progressDialog.dismiss();
            activity.popHint(e.getMessage());
          }

          @Override public void onNext(List<Expense> expenses) {
            HistoryAdapter.this.expenses = expenses;
            notifyDataSetChanged();
          }
        });
  }
}
