package ru.taximer.taxiandroid.network.usecases

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Flowable
import ru.taximer.taxiandroid.network.TaxiApi
import ru.taximer.taxiandroid.network.models.HistoryModel
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.network.models.ResponseException
import ru.taximer.taxiandroid.network.models.SearchTaxiModel
import ru.taximer.taxiandroid.network.models.TaxoparkResultModel


///////////////////////////////////////////////////////////////////////////
// Taxi Usecases
///////////////////////////////////////////////////////////////////////////

interface TaxiUsecases {
    fun getTaxoparcks(
            start: PlaceLocationModel,
            end: PlaceLocationModel,
            carType: Int?,
            isChild: Boolean?,
            isCard: Boolean?,
            isCash: Boolean?): Flowable<TaxoparkResultModel>
    fun getTaxopark(id: Long, hash: String): Flowable<SearchTaxiModel>
    fun getBestTaxopark(hash: String): Flowable<SearchTaxiModel>

    fun getHistory(isStart:Boolean, point: LatLng): Flowable<List<HistoryModel>>
}


///////////////////////////////////////////////////////////////////////////
// Taxi Usecases Impl
///////////////////////////////////////////////////////////////////////////

class TaxiUsecasesImpl(
        private val endpoints: TaxiApi
) : TaxiUsecases {

    override fun getHistory(isStart: Boolean, point: LatLng): Flowable<List<HistoryModel>> =
            endpoints.getHistory(
                    point.latitude,
                    point.longitude
            ).map {
                if(it.success) {
                    if(isStart) it.result!!.to else it.result!!.from
                }else{
                    throw ResponseException(it.errors[0])
                }
            }.applyDefaultNetSchedulers()

    override fun getTaxoparcks(
            start: PlaceLocationModel,
            end: PlaceLocationModel,
            carType: Int?,
            isChild: Boolean?,
            isCard: Boolean?,
            isCash: Boolean?
    ): Flowable<TaxoparkResultModel> =
            endpoints.getTaxoparks(
                    start.latitude,
                    start.longitude,
                    end.latitude,
                    end.longitude,
                    start.address,
                    end.address,
                    isChild,
                    isCash,
                    isCard,
                    carType
            ).map {
                if(it.success) {
                    it.result!!
                }else{
                    throw ResponseException(it.errors[0])
                }
            }.applyDefaultNetSchedulers()

    override fun getTaxopark(
            id: Long,
            hash: String
    ): Flowable<SearchTaxiModel> =
            endpoints.searchCurrentTaxi(
                    id, hash
            ).map {
                if(it.success) {
                    it.result!!.request
                }else{
                    throw ResponseException(it.errors[0])
                }
            }.applyDefaultNetSchedulers()

    override fun getBestTaxopark(
            hash: String
    ): Flowable<SearchTaxiModel> =
            endpoints.searchBesttTaxi(
                    hash
            ).map {
                if(it.success) {
                    it.result!!.request
                }else{
                    throw ResponseException(it.errors[0])
                }
            }.applyDefaultNetSchedulers()
}