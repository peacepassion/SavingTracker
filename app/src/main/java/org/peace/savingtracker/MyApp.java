package org.peace.savingtracker;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import autodagger.AutoComponent;
import autodagger.AutoInjector;
import com.avos.avoscloud.AVOSCloud;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.squareup.leakcanary.RefWatcher;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.utils.AppLogger;
import org.peace.savingtracker.utils.ResUtil;
import org.peace.savingtracker.utils.SystemUtil;
import org.shikato.infodumper.InfoDumperPlugin;
import retrofit.Retrofit;

/**
 * Created by peacepassion on 15/8/11.
 */
@Singleton @AutoComponent(modules = { MyAppModule.class }) @AutoInjector public class MyApp
    extends Application {

  @Inject Retrofit retrofit;

  @Nullable @Inject RefWatcher refWatcher;

  private MyAppComponent appComponent;

  @Override public void onCreate() {
    super.onCreate();

    appComponent = DaggerMyAppComponent.builder().myAppModule(new MyAppModule(this)).build();
    appComponent.inject(this);

    AppLogger.debug = BuildConfig.DEBUG;

    Hawk.init(this)
        .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
        .setStorage(HawkBuilder.newSharedPrefStorage(this))
        .setLogLevel(LogLevel.FULL)
        .build();

    if (BuildConfig.DEBUG) {
      Stetho.initialize(Stetho.newInitializerBuilder(this)
          .enableDumpapp(new MyDumperPluginsProvider(this))
          .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
          .build());
    }

    RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
    Realm.setDefaultConfiguration(configuration);

    ResUtil.init(this);
    SystemUtil.init(this);

    Iconify.with(new FontAwesomeModule());

    AVOSCloud.useAVCloudCN();
    AVOSCloud.initialize(this, "wU2ymMB58FWvLkLAYRTDKt8B", "4uldnECB4QTbWg4lg787cJrA");
  }

  public MyAppComponent getAppComponent() {
    return appComponent;
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
