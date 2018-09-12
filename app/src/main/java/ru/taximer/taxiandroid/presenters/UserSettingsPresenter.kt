package ru.taximer.taxiandroid.presenters

import com.arellomobile.mvp.InjectViewState
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.TaximerApp
import ru.taximer.taxiandroid.network.usecases.UserUsecases
import ru.taximer.taxiandroid.presenters.base.BaseLoadingView
import ru.taximer.taxiandroid.presenters.base.BaseRxPresenter

///////////////////////////////////////////////////////////////////////////
// User Settings Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class UserSettingsPresenter : BaseRxPresenter<Boolean, BaseLoadingView>(){

    val usecases: UserUsecases = TaximerApp.appComponent.baseUsecases()

    fun changeNotifications(value: Boolean){
        usecases.setNotifications(value).subscribe(this)
    }

    override fun onNext(value: Boolean) {
        Prefs.setNotification(value)
    }

    override fun onComplete(){
        //nope
    }
}