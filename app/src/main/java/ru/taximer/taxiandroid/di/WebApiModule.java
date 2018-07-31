package ru.taximer.taxiandroid.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.taximer.taxiandroid.network.TaxiApi;
import ru.taximer.taxiandroid.network.WebService;

@Module(includes = {NetworkModule.class})
public class WebApiModule {
    @Singleton
    @Provides
    WebService provideWebService(TaxiApi api) {return new WebService(api);}
}
