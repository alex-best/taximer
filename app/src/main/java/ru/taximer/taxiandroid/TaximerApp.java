package ru.taximer.taxiandroid;

import android.app.Application;
import android.content.Context;
import ru.taximer.taxiandroid.di.AppComponent;
import ru.taximer.taxiandroid.di.AppModule;
import ru.taximer.taxiandroid.di.DaggerAppComponent;
import ru.taximer.taxiandroid.di.NetworkModule;


public class TaximerApp extends Application {
    private static TaximerApp _INSTANCE = null;
    private AppComponent appComponent;

    public static TaximerApp getInstance() {
        return _INSTANCE;
    }

    @Override
    public void onCreate() {
        _INSTANCE = this;
        super.onCreate();
        appComponent = createAppComponent();
//        LeakCanary.install(this);
    }

    public static Context getContext() {
        return _INSTANCE.getApplicationContext();
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
