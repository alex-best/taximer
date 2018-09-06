package ru.taximer.taxiandroid.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.TaximerApp
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.network.models.TaxoparkResultModel
import ru.taximer.taxiandroid.network.usecases.TaxiUsecases
import ru.taximer.taxiandroid.presenters.base.BaseLoadingView
import ru.taximer.taxiandroid.presenters.base.BaseRxPresenter


///////////////////////////////////////////////////////////////////////////
// Taxopark View
///////////////////////////////////////////////////////////////////////////

@StateStrategyType(AddToEndSingleStrategy::class)
interface TaxoparkView : BaseLoadingView {
    fun setTaxoparkPack(taxoparl: List<Long>, hash: String)
}


///////////////////////////////////////////////////////////////////////////
// Taxopark Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class TaxoparkPresenter : BaseRxPresenter<TaxoparkResultModel, TaxoparkView>() {

    val usecases: TaxiUsecases = TaximerApp.appComponent.baseUsecases()

    fun setPoints(start: PlaceLocationModel, end: PlaceLocationModel){
        viewState.showLoading()
        val carType = if(Prefs.getCarClass() == -1) null else Prefs.getCarClass()
        val isChild = if(Prefs.isChild()) true else null
        val isCard = if(Prefs.isCard()) true else null
        val isCash = if(Prefs.isCash()) true else null
        usecases.getTaxoparcks(start, end, carType, isChild, isCard, isCash).subscribe(this)
    }


    override fun onNext(t: TaxoparkResultModel) {
        viewState.setTaxoparkPack(t.taxoparks.map { it.id }, t.request_hash)
    }

    override fun onComplete() {
        viewState?.hideLoading()
    }
}