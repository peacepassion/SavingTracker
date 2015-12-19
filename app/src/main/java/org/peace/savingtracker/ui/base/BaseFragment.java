package org.peace.savingtracker.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import autodagger.AutoInjector;
import butterknife.ButterKnife;
import com.trello.rxlifecycle.components.support.RxFragment;
import de.greenrobot.event.EventBus;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.MyAppComponent;
import org.peace.savingtracker.model.AVCloudAPI;
import org.peace.savingtracker.user.UserManager;
import retrofit.Retrofit;

/**
 * Created by peacepassion on 15/12/19.
 */
@AutoInjector(MyApp.class) public abstract class BaseFragment extends RxFragment {

  protected BaseActivity activity;

  protected MyAppComponent appComponent;
  protected EventBus eventBus;

  @Inject protected Retrofit retrofit;
  @Inject protected UserManager userManager;
  @Inject protected AVCloudAPI cloudAPI;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    this.activity = (BaseActivity) getActivity();

    eventBus = EventBus.getDefault();
    eventBus.register(this);

    appComponent = ((MyApp) activity.getApplicationContext()).getAppComponent();
    appComponent.inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    int layoutRes = getLayoutRes();
    if (layoutRes > 0) {
      return inflater.inflate(layoutRes, container, false);
    }
    return null;
  }

  abstract protected int getLayoutRes();

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    if (view != null) {
      ButterKnife.bind(this, view);
    }
  }

  protected void popHint(String msg) {
    activity.popHint(msg);
  }

  public void onEvent(String fakeEvent) {

  }
}
