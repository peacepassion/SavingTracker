package org.peace.savingtracker.ui.history;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.model.ExpenseDAO;
import org.peace.savingtracker.model.ExpenseHelper;

/**
 * Created by peacepassion on 15/11/10.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

  private Context context;
  private ExpenseDAO expenseDAO;

  private List<Expense> expenses;

  public HistoryAdapter(Context context, ExpenseDAO expenseDAO) {
    this.context = context;
    this.expenseDAO = expenseDAO;
    expenses = expenseDAO.queryAll();
  }

  public void updateDataSet() {
    expenses = expenseDAO.queryAll();
    notifyDataSetChanged();
  }

  @Override public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(context);
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
      expenseDAO.delete(expense);
      updateDataSet();
    });
  }

  @Override public int getItemCount() {
    return expenses.size();
  }
}
