package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import autodagger.AutoInjector;
import com.avos.avoscloud.AVQuery;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.user.UserManager;
import rx.Observable;

/**
 * Created by peacepassion on 15/12/20.
 */
@AutoExpose(MyApp.class) @AutoInjector(MyApp.class) @Singleton public class AccountBookAPI {

  @Inject AVCloudAPI cloudAPI;
  @Inject UserManager userManager;

  @Inject public AccountBookAPI() {
    MyApp.app.getAppComponent().inject(this);
  }

  public Observable<List<AccountBook>> getOwnedAccountBooks() {
    Observable<List<AccountBook>> ownedBooks = cloudAPI.queryIs(AccountBook.class, //
        AccountBook.OWNER, //
        userManager.getCurrentUser());

    return ownedBooks;
  }

  public Observable<List<AccountBook>> getSharedAccountBooks() {
    AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
    query.whereContainsAll(AccountBook.SHARED_USERS, Arrays.asList(userManager.getCurrentUser()));
    Observable<List<AccountBook>> sharedBooks = cloudAPI.query(query);

    return sharedBooks;
  }

  public Observable<List<AccountBook>> getPermittedAccountBooks() {
    return Observable.zip(getOwnedAccountBooks(), getSharedAccountBooks(),
        (accountBooks, accountBooks2) -> {
          accountBooks.addAll(accountBooks2);
          return accountBooks;
        });
  }
}
