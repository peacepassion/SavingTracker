package org.peace.savingtracker.user;

import android.os.Parcelable;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

/**
 * Created by peacepassion on 15/11/11.
 */
@AVClassName("User") public class User extends AVUser {

  public static final Parcelable.Creator CREATOR = AVObjectCreator.instance;

}
