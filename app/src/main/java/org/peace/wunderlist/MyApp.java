package org.peace.wunderlist;

import android.app.Application;
import android.content.Context;
import com.github.mmin18.layoutcast.LayoutCast;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import me.ele.commons.AppLogger;

/**
 * Created by peacepassion on 15/8/11.
 */
public class MyApp extends Application {

  private RefWatcher refWatcher;

  @Override public void onCreate() {
    super.onCreate();
    AppLogger.debug = true;
    refWatcher = LeakCanary.install(this);

    if (BuildConfig.DEBUG) {
      LayoutCast.init(this);
    }
  }

  public static RefWatcher refWatcher(Context context) {
    MyApp app = (MyApp) context.getApplicationContext();
    return app.refWatcher;
  }
}
