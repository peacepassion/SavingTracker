package org.peace.savingtracker.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/11/12.
 */
public class ProgressDialog extends Dialog {

  private static final String DEFAULT_MESSAGE = "";

  @Bind(R.id.content) TextView contentTV;

  private String msg;


  public ProgressDialog(Context context) {
    this(context, DEFAULT_MESSAGE);
  }

  public ProgressDialog(Context context, String msg) {
    super(context, android.R.style.Theme_Holo_Dialog);
    setContentView(R.layout.dialog_progress);
    ButterKnife.bind(this, this);
    this.msg = msg;
    updateUI();
  }

  private void updateUI() {
    if (TextUtils.isEmpty(msg)) {
      contentTV.setVisibility(View.GONE);
    } else {
      contentTV.setVisibility(View.VISIBLE);
    }
  }
}
