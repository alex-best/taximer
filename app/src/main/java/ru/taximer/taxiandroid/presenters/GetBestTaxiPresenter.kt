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
// Get Best Taxi View
///////////////////////////////////////////////////////////////////////////

@StateStrategyType(AddToEndSingleStrategy::class)
interface GetBestTaxiView : BaseLoadingView {
    fun addBestTaxi(taxi: SearchTaxiModel)
}


///////////////////////////////////////////////////////////////////////////
// Get Best Taxi Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class GetBestTaxiPresenter : BaseRxPresenter<SearchTaxiModel, GetBestTaxiView>() {

    val usecases: TaxiUsecases = TaximerApp.appComponent.baseUsecases()

    fun setHash(hash: String) {
        usecases.getBestTaxopark(hash).subscribe(this)
    }


    @Synchronized
    override fun onNext(t: SearchTaxiModel) {
        viewState.addBestTaxi(t)
    }

    override fun onComplete() {
        //nope
    }
}
