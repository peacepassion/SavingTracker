package org.peace.savingtracker.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import org.peace.savingtracker.ui.base.BaseFragment;

/**
 * Created by peacepassion on 15/12/19.
 */
public abstract class BaseHomeFragment extends BaseFragment {

  protected HomeActivity homeActivity;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    homeActivity = (HomeActivity) activity;
  }

  protected final void setTitle(String title) {
    activity.setTitle(title);
  }

  protected final void setTitle(@StringRes int strRes) {
    activity.setTitle(getText(strRes));
  }
}
