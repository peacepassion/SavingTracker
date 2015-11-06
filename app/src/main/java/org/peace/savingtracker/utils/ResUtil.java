package org.peace.savingtracker.utils;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import org.peace.savingtracker.MyApp;

/**
 * Created by peacepassion on 15/11/6.
 */
public class ResUtil {

  static MyApp myApp;

  public static void init(MyApp myApp) {
    ResUtil.myApp = myApp;
  }

  @ColorInt public static int getColor(@ColorRes int color) {
    return myApp.getResources().getColor(color);
  }
}
