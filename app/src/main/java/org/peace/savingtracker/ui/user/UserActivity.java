package org.peace.savingtracker.ui.user;

import android.os.Bundle;
import android.widget.TextView;
import autodagger.AutoInjector;
import butterknife.Bind;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;

/**
 * Created by peacepassion on 15/11/11.
 */
@AutoInjector(MyApp.class)
public class UserActivity extends BaseActivity {

  @Inject UserManager userManager;

  @Bind(R.id.user_id) TextView userIdTV;
  @Bind(R.id.username) TextView userNameTV;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    initUI();
  }

  private void initUI() {
    if (!userManager.isLogged()) {
      userIdTV.setText("User not logged yet");
      return;
    }
    User user = userManager.getCurrentUser();
    userIdTV.setText("user_id: " + user.getId());
    userNameTV.setText("username: " + user.getUsername());
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_user;
  }
}
