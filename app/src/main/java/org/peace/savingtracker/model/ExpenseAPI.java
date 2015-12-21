package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import autodagger.AutoInjector;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.user.UserManager;
import rx.Observable;

/**
 * Created by peacepassion on 15/12/20.
 */
@AutoExpose(MyApp.class) @AutoInjector(MyApp.class) @Singleton public class ExpenseAPI {

  @Inject AVCloudAPI cloudAPI;
  @Inject UserManager userManager;

  @Inject public ExpenseAPI() {
    MyApp.app.getAppComponent().inject(this);
  }

  public Observable<List<Expense>> getCurrentAccountBookExpenses() {
    if (userManager.getCurrentBook() == null) return Observable.error(new Exception("目前没有选择账本"));

    return cloudAPI.queryIs(Expense.class, //
        Expense.ACCOUNT_BOOK_ID, //
        userManager.getCurrentBook().getObjectId());
  }
}
