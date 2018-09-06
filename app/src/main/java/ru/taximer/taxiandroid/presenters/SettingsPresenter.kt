package ru.taximer.taxiandroid.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.presenters.base.BaseView


///////////////////////////////////////////////////////////////////////////
// Settings View
///////////////////////////////////////////////////////////////////////////

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : BaseView {

    fun setBuisnes()
    fun setEconomy()
    fun setMiniven()
    fun setComfort()
    fun setIsChild(value: Boolean)
    fun setIsCard(value: Boolean)
    fun setIsCAsh(value: Boolean)

}


///////////////////////////////////////////////////////////////////////////
// Settings Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class SettingsPresenter : MvpPresenter<SettingsView>() {

    override fun attachView(view: SettingsView?) {
        super.attachView(view)
        updateState()
    }

    fun setEconomy(){
        setCarClass(1)
    }

    fun setBuisnes(){
        setCarClass(3)
    }

    fun setComfort(){
        setCarClass(2)
    }

    fun setMiniven(){
        setCarClass(4)
    }

    fun setChild(){
        Prefs.setIsChild(!Prefs.isChild())
        updateState()
    }

    fun setCard(value: Boolean){
        if(value == Prefs.isCard()) return
        Prefs.setIsCard(!Prefs.isCard())
        updateState()
    }

    fun setCash(value: Boolean){
        if(value == Prefs.isCash()) return
        Prefs.setIsCash(!Prefs.isCash())
        updateState()
    }

    private fun setCarClass(car: Int){
        Prefs.storeCarClass(car)
        updateState()
    }

    private fun updateState(){
        when(Prefs.getCarClass()){
            1 -> viewState.setEconomy()
            2 -> viewState.setComfort()
            3 -> viewState.setBuisnes()
            4 -> viewState.setMiniven()
        }

        viewState.setIsChild(Prefs.isChild())
        viewState.setIsCAsh(Prefs.isCash())
        viewState.setIsCard(Prefs.isCard())
    }


}