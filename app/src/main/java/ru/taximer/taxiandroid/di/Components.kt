package ru.taximer.taxiandroid.di

import android.content.Context
import dagger.Component
import ru.taximer.taxiandroid.network.TaxiApi
import ru.taximer.taxiandroid.network.usecases.BaseUsecases
import javax.inject.Singleton

///////////////////////////////////////////////////////////////////////////
// App Component
///////////////////////////////////////////////////////////////////////////

@Singleton
@Component(modules = [(AppModule::class), (NetModule::class), (BaseEndpointsModule::class), (BaseUsecasesModule::class)])
interface TaxiAppComponent {
    fun appContext(): Context
    fun baseEndpoints(): TaxiApi
    fun baseUsecases(): BaseUsecases
}