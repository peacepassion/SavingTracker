package org.peace.savingtracker.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/11/6.
 */
public class DebugView extends FrameLayout {

  public DebugView(Context context) {
    super(context);
    init();
  }

  public DebugView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DebugView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_home_debug, this, true);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.check_db) public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.check_db:
        gotoDBActivity();
        break;
      default:
        break;
    }
  }

  private void gotoDBActivity() {
    getContext().startActivity(new Intent(getContext(), DBActivity.class));
  }
}
