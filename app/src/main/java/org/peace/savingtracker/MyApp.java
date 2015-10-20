package org.peace.savingtracker;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.OkHttpClient;
import java.util.ArrayList;
import me.ele.commons.AppLogger;
import org.shikato.infodumper.InfoDumperPlugin;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by peacepassion on 15/8/11.
 */
public class MyApp extends Application {

  private static Retrofit retrofit;

  {
    OkHttpClient client = new OkHttpClient();
    retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(BuildConfig.HOST)
        .client(client)
        .build();
  }

  public static Retrofit getRetrofit() {
    return retrofit;
  }

  private RefWatcher refWatcher;

  @Override public void onCreate() {
    super.onCreate();
    AppLogger.debug = BuildConfig.DEBUG;

    Hawk.init(this)
        .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
        .setStorage(HawkBuilder.newSharedPrefStorage(this))
        .setLogLevel(LogLevel.FULL)
        .build();

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this);
      Stetho.initialize(Stetho.newInitializerBuilder(this)
          .enableDumpapp(new MyDumperPluginsProvider(this))
          .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
          .build());
    }
  }

  public static RefWatcher refWatcher(Context context) {
    MyApp app = (MyApp) context.getApplicationContext();
    return app.refWatcher;
  }

  private static class MyDumperPluginsProvider implements DumperPluginsProvider {
    private final Context mContext;

    public MyDumperPluginsProvider(Context context) {
      mContext = context;
    }

    @Override public Iterable<DumperPlugin> get() {
      ArrayList<DumperPlugin> plugins = new ArrayList<DumperPlugin>();
      for (DumperPlugin defaultPlugin : Stetho.defaultDumperPluginsProvider(mContext).get()) {
        plugins.add(defaultPlugin);
      }
      // Add InfoDumperPlugin
      plugins.add(new InfoDumperPlugin(mContext));
      return plugins;
    }
  }
}
