package org.peace.savingtracker.user;

import autodagger.AutoExpose;
import autodagger.AutoInjector;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.orhanobut.hawk.Hawk;
import de.greenrobot.event.EventBus;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.consts.HawkKeys;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by peacepassion on 15/11/11.
 */
@AutoExpose(MyApp.class) @Singleton public class UserManager {

  @Inject AVCloudAPI cloudAPI;

  private User currentUser;
  private AccountBook currentAccountBook;

  private EventBus eventBus;

  @Inject public UserManager() {
    eventBus = EventBus.getDefault();
  }

  public void logout() {
    AVUser.logOut();
    setCurrentUser(null);
    Hawk.remove(HawkKeys.CURRENT_USER);
  }

  public void setCurrentUser(User user) {
    currentUser = user;
    Hawk.put(HawkKeys.CURRENT_USER, user);
  }

  public void setCurrentAccountBook(AccountBook accountBook) {
    this.currentAccountBook = accountBook;
    Hawk.put(HawkKeys.CURRENT_ACCOUNT_BOOK_ID, currentAccountBook.getObjectId());
    Hawk.put(HawkKeys.CURRENT_ACCOUNT_BOOK_NAME, currentAccountBook.getName());
    eventBus.post(new CurrentAccountBookChangeEvent());
  }

  public AccountBook getCurrentBook() {
    return currentAccountBook;
  }

  public boolean isLogged() {
    return currentUser != null;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public Observable<User> syncCurrentUser() {
    return Observable.create(new Observable.OnSubscribe<User>() {
      @Override public void call(Subscriber<? super User> subscriber) {
        AVQuery<User> query = AVQuery.getQuery(User.class);
        query.getInBackground(currentUser.getObjectId(), new GetCallback<User>() {
          @Override public void done(User user, AVException e) {
            if (e != null) {
              subscriber.onError(e);
              return;
            }
            subscriber.onNext(user);
            subscriber.onCompleted();
          }
        });
      }
    }).map(user -> {
      // todo find a better operator for this condition
      setCurrentUser(user);
      return user;
    });
  }

  public class CurrentAccountBookChangeEvent {

  }
}
