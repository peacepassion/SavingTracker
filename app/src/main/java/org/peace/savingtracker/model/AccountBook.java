package org.peace.savingtracker.model;

import android.os.Parcelable;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by peacepassion on 15/11/19.
 */
@AVClassName("AccountBook") public class AccountBook extends AVObject {

  public static final Parcelable.Creator CREATOR = AVObjectCreator.instance;

  public static final String NAME = "name";
  public static final String OWNER = "owner";
  public static final String DESCRIPTION = "description";

  public AccountBook() {

  }

  public void setName(String name) {
    put(NAME, name);
  }

  public String getName() {
    return getString(NAME);
  }

  public void setOwner(String ownerId) {
    put(OWNER, ownerId);
  }

  public String getOwner() {
    return getString(OWNER);
  }

  public void setDescription(String description) {
    put(DESCRIPTION, description);
  }

  public String getDescription() {
    return getString(DESCRIPTION);
  }
}
