package ru.taximer.taxiandroid.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.taximer.taxiandroid.TaximerApp
import ru.taximer.taxiandroid.network.models.SearchTaxiModel
import ru.taximer.taxiandroid.network.usecases.TaxiUsecases
import ru.taximer.taxiandroid.presenters.base.BaseLoadingView
import ru.taximer.taxiandroid.presenters.base.BaseRxPresenter


///////////////////////////////////////////////////////////////////////////
// Get Taxi View
///////////////////////////////////////////////////////////////////////////

@StateStrategyType(AddToEndSingleStrategy::class)
interface GetTaxiView : BaseLoadingView {
    fun addTaxi(taxi: SearchTaxiModel)
}


///////////////////////////////////////////////////////////////////////////
// Get Taxi Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class GetTaxiPresenter : BaseRxPresenter<SearchTaxiModel, GetTaxiView>() {

    val usecases: TaxiUsecases = TaximerApp.appComponent.baseUsecases()

    fun setTaxoparks(taxoparks: List<Long>, hash: String) {
        taxoparks.map{usecases.getTaxopark(it, hash).subscribe(this@GetTaxiPresenter)}
    }


    @Synchronized
    override fun onNext(t: SearchTaxiModel) {
        viewState.addTaxi(t)
    }

    override fun onComplete() {
        //nope
    }
}