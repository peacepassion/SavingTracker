package org.peace.savingtracker.model;

import android.support.annotation.Nullable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by peacepassion on 15/11/5.
 */
public class Expense extends RealmObject {

  @PrimaryKey private Long id;

  private String userId;

  private String name;

  private long date;

  @Nullable private String category;

  private double value;

  public Expense() {

  }

  public Expense(Long id, String userId, String name, long date, String category, double value) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.date = date;
    this.category = category;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public long getDate() {
    return date;
  }

  public String getCategory() {
    return category;
  }

  public double getValue() {
    return value;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
