package ru.taximer.taxiandroid.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.taximer.taxiandroid.network.Config
import ru.taximer.taxiandroid.network.GSON
import ru.taximer.taxiandroid.network.TaxiApi
import ru.taximer.taxiandroid.network.applyDefaultConfig
import ru.taximer.taxiandroid.network.usecases.BaseUsecases
import javax.inject.Singleton


///////////////////////////////////////////////////////////////////////////
// App Module
///////////////////////////////////////////////////////////////////////////

@Module
class AppModule(private val app: Application) {
    @Provides
    fun provideAppContext(): Context = app
}


///////////////////////////////////////////////////////////////////////////
// Net Module
///////////////////////////////////////////////////////////////////////////

@Module
class NetModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder().applyDefaultConfig().build()

    @Provides
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create(GSON)

    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}


///////////////////////////////////////////////////////////////////////////
// Base Endpoints Module
///////////////////////////////////////////////////////////////////////////

@Module
class BaseEndpointsModule(private val baseUrl: String = Config.BASE_URL) {

    @Provides
    @Singleton
    fun provideBaseEndpoints(okHttpClient: OkHttpClient,
                             converterFactory: Converter.Factory,
                             callAdapterFactory: CallAdapter.Factory
    ): TaxiApi =
            Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(callAdapterFactory)
                    .build()
                    .create(TaxiApi::class.java)


}


///////////////////////////////////////////////////////////////////////////
// Base Usecases Modules
///////////////////////////////////////////////////////////////////////////

@Module
class BaseUsecasesModule {
    @Provides
    @Singleton
    fun provideBaseUsecases(
            endpoints: TaxiApi
    ) = BaseUsecases(endpoints)
}
