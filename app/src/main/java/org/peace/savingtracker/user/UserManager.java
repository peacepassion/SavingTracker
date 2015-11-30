package org.peace.savingtracker.user;

import autodagger.AutoExpose;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.orhanobut.hawk.Hawk;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.consts.HawkKeys;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.model.AccountBook;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by peacepassion on 15/11/11.
 */
@AutoExpose(MyApp.class) @Singleton public class UserManager {

  @Inject AVCloudAPI cloudAPI;

  private AccountBook currentAccountBook;

  private EventBus eventBus;

  @Inject public UserManager() {
    eventBus = EventBus.getDefault();
  }

  public void logout() {
    AVUser.logOut();
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
    return AVUser.getCurrentUser() != null;
  }

  public User getCurrentUser() {
    return AVUser.getCurrentUser(User.class);
  }

  public Observable<User> syncCurrentUser() {
    return Observable.create(new Observable.OnSubscribe<User>() {
      @Override public void call(Subscriber<? super User> subscriber) {
        AVQuery<User> query = AVQuery.getQuery(User.class);
        query.getInBackground(getCurrentUser().getObjectId(), new GetCallback<User>() {
          @Override public void done(User user, AVException e) {
            if (subscriber.isUnsubscribed()) {
              return;
            }
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
      AVUser.changeCurrentUser(user, true);
      return user;
    });
  }

  public class CurrentAccountBookChangeEvent {

  }
}
