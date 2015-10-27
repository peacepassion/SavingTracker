package org.peace.savingtracker.model;

import java.util.List;

/**
 * Created by peacepassion on 15/11/5.
 */
public interface ExpenseDAO {

  void insert(Expense expense);

  void update(Expense expense);

  void delete(Expense expense);

  long getMaxId();

  List<Expense> queryAll();
}
