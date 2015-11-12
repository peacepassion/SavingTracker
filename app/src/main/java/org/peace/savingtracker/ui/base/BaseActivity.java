package org.peace.savingtracker.ui.base;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import autodagger.AutoInjector;
import butterknife.ButterKnife;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.MyAppComponent;
import org.peace.savingtracker.R;
import org.peace.savingtracker.base.dagger.ActivityScope;
import org.peace.savingtracker.ui.login.LoginActivity;
import org.peace.savingtracker.user.UserManager;
import retrofit.Retrofit;

/**
 * Created by peacepassion on 15/10/14.
 */
@AutoInjector({ MyApp.class }) @ActivityScope public abstract class BaseActivity
    extends RxAppCompatActivity {

  protected LinearLayout root;
  protected MyAppComponent appComponent;

  @Inject protected Retrofit retrofit;
  @Inject protected UserManager userManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent = ((MyApp) getApplicationContext()).getAppComponent();
    appComponent.inject(this);
    if (needLogin() && !userManager.isLogged()) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
      return;
    }
    initLayout();
  }

  private void initLayout() {
    if (getLayoutRes() <= 0) {
      return;
    }
    setContentView(R.layout.activity_base);
    root = (LinearLayout) findViewById(R.id.root);
    if (hasTitle()) {
      initToolbar();
      updateStatusBar();
    }
    LayoutInflater.from(this).inflate(getLayoutRes(), root, true);
    ButterKnife.bind(this);
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

  protected boolean allowActionUp() {
    return true;
  }

  protected boolean hasTitle() {
    return true;
  }

  protected boolean needLogin() {
    return false;
  }

  private Toolbar initToolbar() {
    Toolbar toolbar = (Toolbar) LayoutInflater.from(this)
        .inflate(R.layout.include_toolbar, root, true)
        .findViewById(R.id.tool_bar);
    setSupportActionBar(toolbar);
    if (allowActionUp()) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    return toolbar;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
      case android.R.id.home:
        if (allowActionUp()) {
          finish();
          return true;
        }
    }
    return super.onOptionsItemSelected(item);
  }

  protected final void setTitle(String title) {
    if (!hasTitle()) {
      throw new IllegalStateException("this activity has no tool bar");
    }
    getSupportActionBar().setTitle(title);
  }

  public void popHint(String content) {
    popHint(content, false);
  }

  public void popHint(String content, boolean longDuration) {
    Snackbar.make(root, content, longDuration ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT)
        .show();
  }
}
