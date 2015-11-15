package org.peace.savingtracker.model;

import autodagger.AutoExpose;
import com.avos.avoscloud.AVException;
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
@AutoExpose(MyApp.class) @Singleton public class ExpenseAPI {

  @Inject public ExpenseAPI() {

  }

  public Observable<Void> insert(Expense expense) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        expense.saveEventually(new SaveCallback() {
          @Override public void done(AVException e) {
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

  public Observable<Void> update(Expense expense) {
    return insert(expense);
  }

  public Observable<Void> delete(Expense expense) {
    return Observable.create(new Observable.OnSubscribe<Void>() {
      @Override public void call(Subscriber<? super Void> subscriber) {
        expense.deleteEventually(new DeleteCallback() {
          @Override public void done(AVException e) {
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

  public Observable<List<Expense>> queryAll() {
    return Observable.create(new Observable.OnSubscribe<List<Expense>>() {
      @Override public void call(Subscriber<? super List<Expense>> subscriber) {
        AVQuery<Expense> query = AVQuery.getQuery(Expense.class);
        try {
          query.findInBackground(new FindCallback<Expense>() {
            @Override public void done(List<Expense> list, AVException e) {
              if (e != null) {
                subscriber.onError(e);
                return;
              }
              subscriber.onNext(list);
              subscriber.onCompleted();
            }
          });
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    });
  }
}
