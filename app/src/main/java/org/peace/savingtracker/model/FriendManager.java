package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;
import rx.Observable;

/**
 * Created by peacepassion on 15/12/6.
 */
@AutoExpose(MyApp.class) @Singleton public class FriendManager {

  @Inject UserManager userManager;

  @Inject public FriendManager() {

  }

  public Observable<List<User>> getFriends() {
    Observable<List<User>> followersObservable = Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) {
        return;
      }
      User me = userManager.getCurrentUser();
      try {
        AVQuery<User> followerQuery = me.followerQuery(User.class);
        List<User> followers = followerQuery.find();
        subscriber.onNext(followers);
        subscriber.onCompleted();
      } catch (AVException e) {
        subscriber.onError(e);
      }
    });

    Observable<List<User>> followeesObservable = Observable.create(subscriber -> {
      if (subscriber.isUnsubscribed()) {
        return;
      }
      User me = userManager.getCurrentUser();
      try {
        AVQuery<User> followeeQuery = me.followeeQuery(User.class);
        List<User> followees = followeeQuery.find();
        subscriber.onNext(followees);
        subscriber.onCompleted();
      } catch (AVException e) {
        subscriber.onError(e);
      }
    });

    return Observable.zip(followersObservable, followeesObservable, (users, users2) -> {
      List<User> friends = new LinkedList<>();
      for (User follower : users) {
        for (User followee : users2) {
          if (follower.getObjectId().equals(followee.getObjectId())) {
            friends.add(follower);
            break;
          }
        }
      }
      return friends;
    });
  }
}
