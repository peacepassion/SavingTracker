package org.peace.savingtracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by peacepassion on 15/11/10.
 */
public class ExpenseHelper {

  public static String getMonthDay(Expense expense) {
    return getMonthDay(expense.getDate());
  }

  public static String getMonthDay(long mill) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
    return simpleDateFormat.format(new Date(mill));
  }

  public static String getYearMonthDay(Expense expense) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    return simpleDateFormat.format(new Date(expense.getDate()));
  }
}
