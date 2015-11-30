package org.peace.savingtracker;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import autodagger.AutoComponent;
import autodagger.AutoInjector;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.peace.savingtracker.consts.HawkKeys;
import org.peace.savingtracker.model.AccountBook;
import org.peace.savingtracker.model.AddFriendRequest;
import org.peace.savingtracker.model.Expense;
import org.peace.savingtracker.user.User;
import org.peace.savingtracker.user.UserManager;
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

  @Inject UserManager userManager;

  private MyAppComponent appComponent;

  @Override public void onCreate() {
    super.onCreate();

    initDagger();
    initLogger();
    initHawk();
    initStetho();
    initUtils();
    initIconify();
    initLeanCloud();
    initUserInfo();
  }

  private void initDagger() {
    appComponent = DaggerMyAppComponent.builder().myAppModule(new MyAppModule(this)).build();
    appComponent.inject(this);
  }

  private void initLeanCloud() {
    AVOSCloud.useAVCloudCN();
    AVOSCloud.initialize(this, "wU2ymMB58FWvLkLAYRTDKt8B", "4uldnECB4QTbWg4lg787cJrA");
    AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
    AVObject.registerSubclass(Expense.class);
    AVObject.registerSubclass(AccountBook.class);
    AVObject.registerSubclass(AddFriendRequest.class);
    AVUser.alwaysUseSubUserClass(User.class);
    AVUser.registerSubclass(User.class);
  }

  private void initIconify() {
    Iconify.with(new FontAwesomeModule());
  }

  private void initUtils() {
    ResUtil.init(this);
    SystemUtil.init(this);
  }

  private void initStetho() {
    if (BuildConfig.DEBUG) {
      Stetho.initialize(Stetho.newInitializerBuilder(this)
          .enableDumpapp(new MyDumperPluginsProvider(this))
          .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
          .build());
    }
  }

  private void initHawk() {
    Hawk.init(this)
        .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
        .setStorage(HawkBuilder.newSharedPrefStorage(this))
        .setLogLevel(LogLevel.FULL)
        .build();
  }

  private void initUserInfo() {
    String id = Hawk.get(HawkKeys.CURRENT_ACCOUNT_BOOK_ID, null);
    String name = Hawk.get(HawkKeys.CURRENT_ACCOUNT_BOOK_NAME, null);
    if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
      AccountBook accountBook = new AccountBook();
      accountBook.setObjectId(id);
      accountBook.setName(name);
      userManager.setCurrentAccountBook(accountBook);
    }
  }

  private void initLogger() {
    AppLogger.debug = BuildConfig.DEBUG;
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
