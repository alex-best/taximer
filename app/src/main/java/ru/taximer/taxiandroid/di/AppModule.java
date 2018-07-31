package ru.taximer.taxiandroid.di;

import android.app.Application;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application context;

    public AppModule(Application app) {
        this.context = app;
    }

    @Provides
    @Singleton
    Application provideAppContext() {
        return context;
    }

    //TODO add shared_prefs
}
