package org.peace.savingtracker.model;

import io.realm.Realm;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by peacepassion on 15/11/5.
 */
public class ExpenseRealmDAO implements ExpenseDAO {

  private Realm realm;

  public ExpenseRealmDAO(Realm realm) {
    this.realm = realm;
  }

  @Override public void insert(Expense expense) {
    realm.beginTransaction();
    realm.copyToRealm(expense);
    realm.commitTransaction();
  }

  @Override public void update(Expense expense) {
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(expense);
    realm.commitTransaction();
  }

  @Override public void delete(Expense expense) {
    realm.beginTransaction();
    realm.where(Expense.class).equalTo("id", expense.getId()).findAll().clear();
    realm.commitTransaction();
  }

  @Override public long getMaxId() {
    realm.beginTransaction();
    Number number = realm.where(Expense.class).max("id");
    long ret = 0;
    if (number != null) {
      ret = number.longValue();
    }
    realm.commitTransaction();
    return ret;
  }

  @Override public List<Expense> queryAll() {
    List<Expense> rest = new LinkedList<>();
    realm.beginTransaction();
    for (Expense expense : realm.allObjects(Expense.class)) {
      rest.add(expense);
    }
    realm.commitTransaction();
    return rest;
  }
}
