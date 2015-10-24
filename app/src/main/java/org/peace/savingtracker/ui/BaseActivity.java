package org.peace.savingtracker.ui;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.MyAppComponent;
import org.peace.savingtracker.R;

/**
 * Created by peacepassion on 15/10/14.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

  protected LinearLayout root;
  protected MyAppComponent appComponent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent = ((MyApp) getApplicationContext()).getAppComponent();
    initLayout();
  }

  private void initLayout() {
    if (getLayoutRes() > 0) {
      setContentView(R.layout.activity_base);
      root = (LinearLayout) findViewById(R.id.root);
      if (hasTitle()) {
        inflateToolbar();
        updateStatusBar();
      }
      LayoutInflater.from(this).inflate(getLayoutRes(), root, true);
      ButterKnife.bind(this);
    }
  }

  protected void updateStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window window = getWindow();
      WindowManager.LayoutParams params = window.getAttributes();
      int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      params.flags |= bits;
      window.setAttributes(params);
      SystemBarTintManager manager = new SystemBarTintManager(this);
      manager.setStatusBarTintEnabled(true);
      TypedValue typedValue = new TypedValue();
      Resources.Theme theme = getTheme();
      theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
      manager.setStatusBarTintColor(typedValue.data);
    }
  }

  @LayoutRes abstract protected int getLayoutRes();

  protected boolean hasTitle() {
    return true;
  }

  private Toolbar inflateToolbar() {
    Toolbar toolbar = (Toolbar) LayoutInflater.from(this)
        .inflate(R.layout.include_toolbar, root, true)
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
