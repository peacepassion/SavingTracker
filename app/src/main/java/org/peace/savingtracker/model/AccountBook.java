package org.peace.savingtracker.model;

import android.os.Parcelable;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import java.util.List;
import org.peace.savingtracker.user.User;

/**
 * Created by peacepassion on 15/11/19.
 */
@AVClassName("AccountBook") public class AccountBook extends AVObject {

  public static final Parcelable.Creator CREATOR = AVObjectCreator.instance;

  public static final String NAME = "name";
  public static final String OWNER = "owner";
  public static final String DESCRIPTION = "description";
  public static final String SHARED_USERS = "shared_users";

  public AccountBook() {

  }

  public void setName(String name) {
    put(NAME, name);
  }

  public String getName() {
    return getString(NAME);
  }

  public void setOwner(User owner) {
    put(OWNER, owner);
  }

  public User getOwner() {
    return getAVUser(OWNER, User.class);
  }

  public void addSharedUser(User user) {
    add(SHARED_USERS, user);
  }

  public List<User> getSharedUsers() {
    return getList(SHARED_USERS);
  }

  public void setDescription(String description) {
    put(DESCRIPTION, description);
  }

  public String getDescription() {
    return getString(DESCRIPTION);
  }
}
