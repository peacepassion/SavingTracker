package org.peace.savingtracker;

import android.support.annotation.Nullable;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by peacepassion on 15/10/24.
 */
@Module
public class MyAppModule {
  private MyApp app;

  MyAppModule(MyApp app) {
    this.app = app;
  }

  @Provides @Singleton public Retrofit providesRetrofit() {
    OkHttpClient client = new OkHttpClient();
    return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(BuildConfig.HOST)
        .client(client)
        .build();
  }

  @Provides @Singleton @Nullable public RefWatcher providesRefWatcher() {
    if (BuildConfig.DEBUG) {
      return LeakCanary.install(app);
    }
    return null;
  }
}
