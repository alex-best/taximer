package ru.taximer.taxiandroid


import android.support.multidex.MultiDexApplication
import com.crashlytics.android.answers.Answers
import com.orhanobut.hawk.Hawk
import io.fabric.sdk.android.Fabric
import ru.taximer.taxiandroid.di.AppModule
import ru.taximer.taxiandroid.di.DaggerTaxiAppComponent
import ru.taximer.taxiandroid.di.TaxiAppComponent
import kotlin.system.measureTimeMillis


class TaximerApp : MultiDexApplication() {

    companion object {
        private lateinit var _appModule: AppModule
        val appModule: AppModule
            get() = _appModule

        private val _appComponent: TaxiAppComponent by lazy {
            DaggerTaxiAppComponent.builder()
                    .appModule(appModule)
                    .build()
        }

        @JvmStatic
        val appComponent: TaxiAppComponent
            get() = _appComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDagger()
        Hawk.init(this).build()
        Fabric.with(this, Answers())
    }

    private fun initDagger() {
        measureTimeMillis {
            _appModule = AppModule(this)
        }
    }
}
