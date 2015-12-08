package org.peace.savingtracker.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FollowCallback;
import java.util.LinkedList;
import java.util.List;
import org.peace.savingtracker.R;
import org.peace.savingtracker.model.AddFriendRequest;
import org.peace.savingtracker.ui.base.BaseActivity;
import org.peace.savingtracker.ui.widget.ProgressDialog;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.utils.AppLogger;
import org.peace.savingtracker.utils.ResUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by peacepassion on 15/12/2.
 */
public class MessageCenterActivity extends BaseActivity {

  @Bind(R.id.list_view) ListView listView;

  private MessageAdapter adapter;
  private List<AddFriendRequest> addFriendRequestList;

  @Override protected int getLayoutRes() {
    return R.layout.activity_message_center;
  }

  @Override protected boolean needLogin() {
    return true;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(getString(R.string.message_center));

    setUpListView();
    requestMessages();
  }

  private void setUpListView() {
    addFriendRequestList = new LinkedList<>();
    adapter = new MessageAdapter(this);
    listView.setAdapter(adapter);
  }

  private void requestMessages() {
    ProgressDialog dlg = new ProgressDialog(this);

    AVQuery<AddFriendRequest> toMeQuery = AVQuery.getQuery(AddFriendRequest.class);
    toMeQuery.whereEqualTo(AddFriendRequest.TO_USER, userManager.getCurrentUser());
    toMeQuery.include(AddFriendRequest.FROM_USER);
    toMeQuery.include(AddFriendRequest.TO_USER);
    Observable<List<AddFriendRequest>> toMe = cloudAPI.query(toMeQuery);

    AVQuery<AddFriendRequest> fromMeQuery = AVQuery.getQuery(AddFriendRequest.class);
    fromMeQuery.whereEqualTo(AddFriendRequest.FROM_USER, userManager.getCurrentUser());
    toMeQuery.include(AddFriendRequest.FROM_USER);
    toMeQuery.include(AddFriendRequest.TO_USER);
    Observable<List<AddFriendRequest>> fromMe = cloudAPI.query(fromMeQuery);

    Observable.concat(toMe, fromMe)
        .subscribeOn(Schedulers.io())
        .flatMap(addFriendRequests -> Observable.from(addFriendRequests))
        .filter(addFriendRequest -> {
          String meId = userManager.getCurrentUser().getObjectId();
          if (addFriendRequest.getFromUser().getObjectId().equals(meId)
              && addFriendRequest.getStatus() == AddFriendRequest.STATUS_WAIT) {
            return false;
          }
          return true;
        })
        .distinct(addFriendRequest -> {
          StringBuilder sb = new StringBuilder();
          sb.append(addFriendRequest.getFromUser().getObjectId())
              .append(" ")
              .append(addFriendRequest.getToUser().getObjectId());
          return sb.toString();
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<AddFriendRequest>() {
          @Override public void onStart() {
            dlg.show();
          }

          @Override public void onCompleted() {
            dlg.dismiss();
            adapter.setRequestList(addFriendRequestList);
          }

          @Override public void onError(Throwable e) {
            dlg.dismiss();
            popHint(e.getMessage());
          }

          @Override public void onNext(AddFriendRequest addFriendRequest) {
            addFriendRequestList.add(addFriendRequest);
          }
        });
  }

  @OnItemLongClick(R.id.list_view) public boolean onListViewLongClick(View v, int position) {
    AddFriendRequest request = addFriendRequestList.get(position);
    int type = adapter.getItemViewType(position);
    if (type == MessageType.UNHANDLED_ADD_FRIEND_INVITATION.value) {
      alertContextDialog(request);
    }
    return true;
  }

  private void alertContextDialog(final AddFriendRequest request) {
    new MaterialDialog.Builder(this).items(new CharSequence[] {
        getString(R.string.accept_invitation), getString(R.string.reject_invitation)
    }).itemsCallback((dialog, itemView, which, text) -> {

      if (which == 1) {
        request.setStatus(AddFriendRequest.STATUS_REJECT);
      } else {
        request.setStatus(AddFriendRequest.STATUS_ACCEPT);
      }

      cloudAPI.update(request)
          .subscribeOn(Schedulers.io())
          .observeOn(Schedulers.io())
          .flatMap(aVoid -> Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
              userManager.getCurrentUser()
                  .followInBackground(request.getFromUser().getObjectId(), new FollowCallback() {
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
            ProgressDialog dlg = new ProgressDialog(MessageCenterActivity.this);

            @Override public void onStart() {
              dlg.show();
            }

            @Override public void onCompleted() {
              dlg.dismiss();
              if (which == 0) {
                popHint(getString(R.string.add_friend_succ));
              } else {
                popHint(getString(R.string.reject_invitation_succ));
              }
            }

            @Override public void onError(Throwable e) {
              dlg.dismiss();
              popHint(e.getMessage());
            }

            @Override public void onNext(Void aVoid) {

            }
          });
    }).build().show();
  }

  private static class MessageAdapter extends BaseAdapter {

    private MessageCenterActivity activity;
    private LayoutInflater inflater;

    private List<AddFriendRequest> requestList;

    private MessageAdapter(MessageCenterActivity activity) {
      this.activity = activity;
      inflater = LayoutInflater.from(activity);
      requestList = new LinkedList<>();
    }

    private void setRequestList(List<AddFriendRequest> requestList) {
      this.requestList = requestList;
      notifyDataSetChanged();
    }

    @Override public int getViewTypeCount() {
      return MessageType.values().length;
    }

    @Override public int getItemViewType(int position) {
      AddFriendRequest request = requestList.get(position);
      if (request.getStatus() == AddFriendRequest.STATUS_WAIT) {
        return MessageType.UNHANDLED_ADD_FRIEND_INVITATION.value;
      }
      if (request.getStatus() == AddFriendRequest.STATUS_ACCEPT) {
        return MessageType.ADD_FRIEND_INVITATION_SUCC.value;
      }
      if (request.getStatus() == AddFriendRequest.STATUS_REJECT) {
        return MessageType.ADD_FRIEND_INVITATION_FAIL.value;
      }
      return -1;
    }

    @Override public int getCount() {
      return requestList.size();
    }

    @Override public Object getItem(int position) {
      return requestList.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder vh;
      if (convertView == null) {
        convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        vh = new ViewHolder(convertView);
        convertView.setTag(vh);
      } else {
        vh = (ViewHolder) convertView.getTag();
      }

      AddFriendRequest request = (AddFriendRequest) getItem(position);
      int type = getItemViewType(position);
      if (type == MessageType.UNHANDLED_ADD_FRIEND_INVITATION.value) {
        vh.tv.setText(activity.getString(R.string.receive_add_friend_invitation,
            request.getFromUser().getUsername()));
      } else if (type == MessageType.ADD_FRIEND_INVITATION_SUCC.value) {
        vh.tv.setText(MessageType.ADD_FRIEND_INVITATION_SUCC.des);
      } else if (type == MessageType.ADD_FRIEND_INVITATION_FAIL.value) {
        vh.tv.setText(MessageType.ADD_FRIEND_INVITATION_FAIL.des);
      }

      return convertView;
    }
  }

  public static class ViewHolder {
    @Bind(android.R.id.text1) public TextView tv;

    private ViewHolder(View item) {
      ButterKnife.bind(this, item);
    }
  }

  private enum MessageType {
    UNHANDLED_ADD_FRIEND_INVITATION(0, ""),
    ADD_FRIEND_INVITATION_SUCC(1, ResUtil.getString(R.string.add_friend_succ)),
    ADD_FRIEND_INVITATION_FAIL(2, ResUtil.getString(R.string.add_friend_fail));

    private int value;
    private String des;

    MessageType(int v, String des) {
      value = v;
      this.des = des;
    }
  }
}
