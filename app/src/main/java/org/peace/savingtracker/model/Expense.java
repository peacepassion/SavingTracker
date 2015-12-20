package org.peace.savingtracker.model;

import android.os.Parcelable;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by peacepassion on 15/11/5.
 */
@AVClassName("Expense") public class Expense extends AVObject {

  public static final Parcelable.Creator CREATOR = AVObjectCreator.instance;

  public static final String USER_ID = "user_id";
  public static final String USERNAME = "username";
  public static final String DATE = "date";
  public static final String CATEGORY = "category";
  public static final String VALUE = "value";
  public static final String ACCOUNT_BOOK_ID = "account_book_id";

  public Expense() {

  }

  public Expense(String userId, //
      String name, //
      long date, //
      String category, //
      double value,//
      String accountBookId) {
    setUserId(userId);
    setName(name);
    setDate(date);
    setCategory(category);
    setValue(value);
    setAccountBookId(accountBookId);
  }

  public String getUserId() {
    return getString(USER_ID);
  }

  public String getName() {
    return getString(USERNAME);
  }

  public long getDate() {
    return getLong(DATE);
  }

  public String getCategory() {
    return getString(CATEGORY);
  }

  public String getAccoutntBookId() {
    return getString(ACCOUNT_BOOK_ID);
  }

  public double getValue() {
    return Double.valueOf(get(VALUE).toString());
  }

  public void setUserId(String userId) {
    put(USER_ID, userId);
  }

  public void setName(String name) {
    put(USERNAME, name);
  }

  public void setDate(long date) {
    put(DATE, date);
  }

  public void setCategory(String category) {
    put(CATEGORY, category);
  }

  public void setValue(double value) {
    put(VALUE, value);
  }

  public void setAccountBookId(String id) {
    put(ACCOUNT_BOOK_ID, id);
  }
}
