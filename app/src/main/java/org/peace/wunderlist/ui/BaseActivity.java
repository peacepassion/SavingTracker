package org.peace.wunderlist.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import org.peace.wunderlist.R;

/**
 * Created by peacepassion on 15/10/14.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

  @Bind(R.id.root) protected LinearLayout root;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getLayoutRes() > 0) {
      setContentView(R.layout.activity_base);
      ButterKnife.bind(this);
      View content = LayoutInflater.from(this).inflate(getLayoutRes(), root, false);
      if (hasTitle()) {
        Toolbar toolbar = inflateToolbar();
        root.addView(toolbar);
      }
      root.addView(content);
    }
  }

  @LayoutRes abstract protected int getLayoutRes();

  protected boolean hasTitle() {
    return true;
  }

  private Toolbar inflateToolbar() {
    Toolbar toolbar = (Toolbar) LayoutInflater.from(this)
        .inflate(R.layout.include_toolbar, null, false)
        .findViewById(R.id.tool_bar);
    setSupportActionBar(toolbar);
    return toolbar;
  }

  protected final void setTitle(String title) {
    if (!hasTitle()) {
      throw new IllegalStateException("this activity has no tool bar");
    }
    getSupportActionBar().setTitle(title);
  }
}
