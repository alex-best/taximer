package ru.taximer.taxiandroid.di;

import javax.inject.Singleton;
import dagger.Component;
import ru.taximer.taxiandroid.ui.MainActivity;

@Singleton
@Component(modules = { AppModule.class, WebApiModule.class})
public interface AppComponent {
    void inject(MainActivity target);
}
