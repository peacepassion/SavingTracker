package org.peace.savingtracker.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.OnItemLongClick;
import butterknife.OnLongClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FollowCallback;
import com.jakewharton.rxbinding.view.RxView;
import java.util.LinkedList;
import java.util.List;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AddFriendRequest;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.utils.ResUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/11/30.
 */
public class SearchUserActivity extends BaseActivity {

  @Bind(R.id.username) EditText usernameET;
  @Bind(R.id.confirm) Button confirmBtn;
  @Bind(R.id.list_view) ListView listView;

  private ArrayAdapter<String> adapter;
  private List<User> searchedUsers;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initSearchResultList();
    initConfirmBtn();
  }

  private void initSearchResultList() {
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    listView.setAdapter(adapter);
  }

  private void initConfirmBtn() {
    RxView.clickEvents(confirmBtn)
        .filter(viewClickEvent -> validateUsername())
        .subscribe(viewClickEvent -> {
          cloudAPI.queryContains(User.class, "username", usernameET.getText().toString())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .flatMap(users -> Observable.from(users))
              .filter(user -> {
                if (user.getObjectId().equals(userManager.getCurrentUser().getObjectId())) {
                  return false;
                }
                return true;
              })
              .map(user -> user.getUsername())
              .subscribe(new Subscriber<String>() {
                ProgressDialog progressDialog = new ProgressDialog(SearchUserActivity.this);

                @Override public void onStart() {
                  adapter.clear();
                  progressDialog.show();
                }

                @Override public void onCompleted() {
                  progressDialog.dismiss();
                }

                @Override public void onError(Throwable e) {
                  progressDialog.dismiss();
                  popHint(e.getMessage());
                }

                @Override public void onNext(String username) {
                  adapter.add(username);
                }
              });
        });
  }

  @OnItemLongClick(R.id.list_view) public boolean onListViewLongClick(View v, int position) {
    new MaterialDialog.Builder(this).items(new CharSequence[] { getString(R.string.invite_friend) })
        .itemsCallback((dialog, itemView, which, text) -> {
          AddFriendRequest request = new AddFriendRequest();
          request.setFromUser(userManager.getCurrentUser());
          request.setToUser(searchedUsers.get(position));
          request.setStatus(AddFriendRequest.STATUS_WAIT);
          cloudAPI.insert(request)
              .subscribeOn(Schedulers.io())
              .observeOn(Schedulers.io())
              .flatMap(aVoid -> Observable.create(new Observable.OnSubscribe<Void>() {
                @Override public void call(Subscriber<? super Void> subscriber) {
                  User me = userManager.getCurrentUser();
                  me.followInBackground(request.getToUser().getObjectId(), new FollowCallback() {
                    @Override public void done(AVObject avObject, AVException e) {
                      if (subscriber.isUnsubscribed()) {
                        return;
                      }
                      if (e != null) {
                        subscriber.onError(e);
                        return;
                      }
                      subscriber.onNext(null);
                      subscriber.onCompleted();
                    }
                  });
                }
              }))
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Subscriber<Void>() {
                ProgressDialog dlg = new ProgressDialog(SearchUserActivity.this);

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

                @Override public void onNext(Void aVoid) {
                  popHint(getString(R.string.add_friend_invitation_sent));
                }
              });
        })
        .show();
    return true;
  }

  private Boolean validateUsername() {
    if (TextUtils.isEmpty(usernameET.getText().toString())) {
      popHint("Username cannot be empty.");
      return false;
    }
    return true;
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override protected int getLayoutRes() {
    return R.layout.activity_search_user;
  }
  //
  //static class SearchResultAdapter extends BaseAdapter {
  //
  //  private List<User> userList;
  //  private LayoutInflater inflater;
  //  private Activity activity;
  //
  //  SearchResultAdapter(Activity activity) {
  //    this.activity = activity;
  //    inflater = LayoutInflater.from(activity);
  //    userList = new LinkedList<>();
  //  }
  //
  //  @Override public int getCount() {
  //    return userList.size();
  //  }
  //
  //  @Override public Object getItem(int position) {
  //    return userList.get(position);
  //  }
  //
  //  @Override public long getItemId(int position) {
  //    return position;
  //  }
  //
  //  @Override public View getView(int position, View convertView, ViewGroup parent) {
  //
  //  }
  //}
}
