package org.peace.savingtracker.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

/**
 * Created by peacepassion on 15/11/12.
 */
public class ProgressDialog {

  private static final String DEFAULT_MESSAGE = "Loading...";

  private MaterialDialog dialog;

  public ProgressDialog(Context context) {
    this(context, DEFAULT_MESSAGE);
  }

  public ProgressDialog(Context context, String msg) {
    MaterialDialog.Builder builder =
        new MaterialDialog.Builder(context).theme(Theme.DARK).progress(true, 100);
    if (!TextUtils.isEmpty(msg)) {
      builder.content(msg);
    }
    dialog = builder.build();
  }

  public void show() {
    dialog.show();
  }

  public void dismiss() {
    dialog.dismiss();
  }
}
