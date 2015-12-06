package org.peace.savingtracker.ui.user;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import autodagger.AutoInjector;
import butterknife.Bind;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.FriendManager;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/12/6.
 */
@AutoInjector(MyApp.class) public class FriendListActivity extends BaseActivity {

  @Inject FriendManager friendManager;

  @Bind(R.id.list_view) ListView listView;

  private ArrayAdapter<String> adapter;
  private List<User> friends;

  @Override protected int getLayoutRes() {
    return R.layout.activity_friend_list;
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appComponent.inject(this);
    setTitle(getString(R.string.friend_list));

    setUpList();
  }

  private void setUpList() {
    friends = new LinkedList<>();
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    listView.setAdapter(adapter);
    requestFriendList();
  }

  private void requestFriendList() {
    friendManager.getFriends().
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(users -> {
      List<String> userId = new ArrayList<>(users.size());
      for (User user : users) {
        userId.add(user.getObjectId());
      }
      return userId;
    }).subscribe(new Subscriber<List<String>>() {
      ProgressDialog dlg = new ProgressDialog(FriendListActivity.this);

      @Override public void onStart() {
        dlg.show();
      }

      @Override public void onCompleted() {
        dlg.dismiss();
      }

      @Override public void onError(Throwable e) {
        dlg.dismiss();
        popHint(e.getMessage());
      }

      @Override public void onNext(List<String> userIds) {
        adapter.clear();
        adapter.addAll(userIds);
      }
    });
  }
}
