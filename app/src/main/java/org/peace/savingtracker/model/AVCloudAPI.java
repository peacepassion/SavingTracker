package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.MyApp;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by peacepassion on 15/11/12.
 */
@AutoExpose(MyApp.class) @Singleton public class AVCloudAPI {

  @Inject public AVCloudAPI() {

  }

  public <T extends AVObject> Observable<Void> insert(T obj) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        obj.saveEventually(new SaveCallback() {
          @Override public void done(AVException e) {
            if (subscriber.isUnsubscribed()) {
              return;
            }
            if (e == null) {
              subscriber.onNext(null);
              subscriber.onCompleted();
              return;
            }
            subscriber.onError(e);
          }
        });
      }
    });
  }

  public <T extends AVObject> Observable<Void> update(T obj) {
    return insert(obj);
  }

  public <T extends AVObject> Observable<Void> delete(T obj) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        obj.deleteEventually(new DeleteCallback() {
          @Override public void done(AVException e) {
            if (subscriber.isUnsubscribed()) {
              return;
            }
            if (e == null) {
              subscriber.onNext(null);
              subscriber.onCompleted();
              return;
            }
            subscriber.onError(e);
          }
        });
      }
    });
  }

  public <T extends AVObject> Observable<List<T>> query(AVQuery<T> q) {
    return Observable.create(new Observable.OnSubscribe<List<T>>() {
      @Override public void call(Subscriber<? super List<T>> subscriber) {
        q.findInBackground(new FindCallback<T>() {
          @Override public void done(List<T> list, AVException e) {
            if (subscriber.isUnsubscribed()) {
              return;
            }
            if (e != null) {
              subscriber.onError(e);
              return;
            }
            subscriber.onNext(list);
            subscriber.onCompleted();
          }
        });
      }
    });
  }

  public <T extends AVObject> Observable<List<T>> queryIs(Class<T> clazz, String key,
      Object target) {
    AVQuery<T> query = AVQuery.getQuery(clazz);
    query.whereEqualTo(key, target);
    return query(query);
  }

  public <T extends AVObject> Observable<List<T>> queryContains(Class<T> clazz, String key,
      String subString) {
    AVQuery<T> query = AVQuery.getQuery(clazz);
    query.whereContains(key, subString);
    return query(query);
  }

  public <T extends AVObject> Observable<List<T>> queryAll(Class<T> clazz) {
    AVQuery<T> query = AVQuery.getQuery(clazz);
    return query(query);
  }
}
