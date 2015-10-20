package org.peace.savingtracker.user;

import autodagger.AutoExpose;
import com.orhanobut.hawk.Hawk;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.consts.HawkKeys;

/**
 * Created by peacepassion on 15/11/11.
 */
@AutoExpose(MyApp.class) @Singleton public class UserManager {

  private User currentUser;

  @Inject public UserManager() {

  }

  public void qqUserLogin(String openId, String token, String username) {
    User user = new User();
    user.setId(openId);
    user.setUsername(username);
    login(user);
    Hawk.put(HawkKeys.QQ_TOKEN, token);
  }

  public void login(User user) {
    currentUser = user;
    Hawk.put(HawkKeys.CURRENT_USER, user);
  }

  public boolean isLogged() {
    return currentUser != null;
  }

  public User getCurrentUser() {
    return currentUser;
  }
}
