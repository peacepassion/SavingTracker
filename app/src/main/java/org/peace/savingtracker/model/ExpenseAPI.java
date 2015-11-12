package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;

/**
 * Created by peacepassion on 15/11/12.
 */
@AutoExpose(MyApp.class) @Singleton public class ExpenseAPI {

  @Inject public ExpenseAPI() {

  }

  public void insert(Expense expense, SaveCallback callback) {
    expense.saveEventually(callback);
  }

  public void update(Expense expense, SaveCallback callback) {
    expense.saveEventually(callback);
  }

  public void delete(Expense expense, DeleteCallback callback) {
    expense.deleteEventually(callback);
  }

  public void queryAll(FindCallback<Expense> callback) {
    AVQuery<Expense> query = AVQuery.getQuery(Expense.class);
    try {
      query.findInBackground(callback);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
