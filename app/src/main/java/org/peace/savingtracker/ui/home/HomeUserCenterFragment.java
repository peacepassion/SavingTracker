package org.peace.savingtracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.OnClick;
import org.peace.savingtracker.R;
import org.peace.savingtracker.ui.AddExpenseActivity;
import org.peace.savingtracker.ui.accountbook.AddAccountBookActivity;
import org.peace.savingtracker.ui.accountbook.SelectAccountBookActivity;
import org.peace.savingtracker.ui.user.FriendListActivity;
import org.peace.savingtracker.ui.user.MessageCenterActivity;
import org.peace.savingtracker.ui.user.SearchUserActivity;
import org.peace.savingtracker.ui.user.UserActivity;

/**
 * Created by peacepassion on 15/12/19.
 */
public class HomeUserCenterFragment extends BaseHomeFragment {

  public static HomeUserCenterFragment newInstance() {
    return new HomeUserCenterFragment();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setTitle(R.string.user_center);
  }

  @OnClick({
      R.id.user_center, R.id.add_account_book, R.id.select_account_book, R.id.search_user,
      R.id.message_center, R.id.friend_list
  }) public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.user_center:
        startActivity(new Intent(activity, UserActivity.class));
        break;
      case R.id.add_account_book:
        startActivity(new Intent(activity, AddAccountBookActivity.class));
        break;
      case R.id.select_account_book:
        startActivity(new Intent(activity, SelectAccountBookActivity.class));
        break;
      case R.id.search_user:
        startActivity(new Intent(activity, SearchUserActivity.class));
        break;
      case R.id.message_center:
        startActivity(new Intent(activity, MessageCenterActivity.class));
        break;
      case R.id.friend_list:
        startActivity(new Intent(activity, FriendListActivity.class));
        break;
      default:
        break;
    }
  }

  @Override protected int getLayoutRes() {
    return R.layout.fragment_home_user_center;
  }
}
