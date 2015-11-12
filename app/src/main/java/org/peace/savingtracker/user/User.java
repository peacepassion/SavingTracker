package org.peace.savingtracker.user;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

/**
 * Created by peacepassion on 15/11/11.
 */
public class User {

  // todo: add group property

  AVUser avUser;

  public User() {
    this.avUser = new AVUser();
  }

  public User(AVUser avUser) {
    this.avUser = avUser;
  }

  public String getUsername() {
    return avUser.getUsername();
  }

  public void setUsername(String username) {
    avUser.setUsername(username);
  }

  public void setPassword(String password) {
    avUser.setPassword(password);
  }

  public void setEmail(String email) {
    avUser.setEmail(email);
  }

  public String getId() {
    return avUser.getObjectId();
  }

  public void signUpInBackground(SignUpCallback callback) {
    avUser.signUpInBackground(callback);
  }
}
