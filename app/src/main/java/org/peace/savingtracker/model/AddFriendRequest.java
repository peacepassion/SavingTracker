package org.peace.savingtracker.model;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import org.peace.savingtracker.user.User;

/**
 * Created by peacepassion on 15/12/2.
 */
@AVClassName("AddFriendRequest")
public class AddFriendRequest extends AVObject{

  public static final int STATUS_WAIT = 0;
  public static final int STATUS_ACCEPT = 1;
  public static final int STATUS_REJECT = 2;

  public static final String FROM_USER = "from_user";
  public static final String TO_USER = "to_user";
  public static final String STATUS = "status";

  public User getFromUser() {
    return getAVUser(FROM_USER);
  }

  public void setFromUser(User fromUser) {
    put(FROM_USER, fromUser);
  }

  public User getToUser() {
    return getAVUser(TO_USER);
  }

  public void setToUser(User toUser) {
    put(TO_USER, toUser);
  }

  public void setStatus(int status) {
    put(STATUS, status);
  }

  public int getStatus() {
    return getInt(STATUS);
  }
}
