package org.peace.savingtracker.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import org.peace.savingtracker.MyApp;

/**
 * Created by peacepassion on 15/11/10.
 */
public class SystemUtil {

  static MyApp myApp;

  public static void init(MyApp myApp) {
    SystemUtil.myApp = myApp;
  }

  public static void hideKeyboard(EditText editText) {
    InputMethodManager imm =
        (InputMethodManager) myApp.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
  }
}
