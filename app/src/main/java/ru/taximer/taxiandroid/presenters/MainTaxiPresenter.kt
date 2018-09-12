package ru.taximer.taxiandroid.presenters

import android.location.Address
import android.location.Geocoder
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.ui.FallbackReverseGeocoderObservable
import ru.taximer.taxiandroid.utils.isNotNullOrEmpty


///////////////////////////////////////////////////////////////////////////
// Main Taxi View
///////////////////////////////////////////////////////////////////////////

interface MainTaxiView : MvpView {
    fun setStart(location: PlaceLocationModel?)
    fun setEnd(location: PlaceLocationModel?)
    fun setState(isStartEdit: Boolean, isBothLocationContaints: Boolean)
    fun startSearch(start: PlaceLocationModel, end: PlaceLocationModel)
}


///////////////////////////////////////////////////////////////////////////
// MainTaxi Presenter
///////////////////////////////////////////////////////////////////////////

@InjectViewState
class MainTaxiPresenter : MvpPresenter<MainTaxiView>() {
    private var startLocation: PlaceLocationModel? = null
    private var endLocation: PlaceLocationModel? = null

    var isStartEdit: Boolean = false
    private var isEndEdit: Boolean = false

    private var addressDisposable: Disposable? = null

    override fun attachView(view: MainTaxiView?) {
        super.attachView(view)
        viewState.setStart(startLocation)
        viewState.setEnd(endLocation)
        setEditState()
    }

    override fun onDestroy() {
        super.onDestroy()
        addressDisposable?.dispose()
        addressDisposable = null
    }

    fun getSearchLocation(): LatLng {
        if (isStartEdit) {
            return LatLng(endLocation?.latitude ?: 0.0, endLocation?.longitude ?: 0.0)
        }
        else {
            return LatLng(startLocation?.latitude ?: 0.0, startLocation?.longitude ?: 0.0)
        }
    }

    fun editStart() {
        isEndEdit = false
        isStartEdit = true
    }

    fun editEnd() {
        isStartEdit = false
        isEndEdit = true
    }

    private fun setEditState() {
        if(!isEndEdit && endLocation == null){
            editEnd()
        }
        viewState.setState(isStartEdit, startLocation != null && endLocation != null)
    }

    fun setLocation(location: PlaceLocationModel) {
        if (isStartEdit) {
            startLocation = location
            viewState.setStart(startLocation)
            isStartEdit = false
        }
        else {
            endLocation = location
            viewState.setEnd(endLocation)
            isEndEdit = false
        }
        setEditState()
    }

    fun onSearch() {
        viewState?.startSearch(startLocation!!, endLocation!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // search address stuff
    ///////////////////////////////////////////////////////////////////////////

    fun detectAddress(coordinates: LatLng, address: String) {
        val place = PlaceLocationModel(coordinates.latitude, coordinates.longitude, address)
        setLocation(place)
    }

    fun detectAddress(coordinates: LatLng, geocoder: Geocoder) {
        addressDisposable?.dispose()

        addressDisposable = Observable.fromCallable<PlaceLocationModel> {
            val addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
            getPlaceLocationModel(addresses, coordinates)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setLocation(it) },
                        { detectFallbackReverseGeocode(coordinates) })
    }

    private fun detectFallbackReverseGeocode(coordinates: LatLng) {
        addressDisposable?.dispose()

        addressDisposable = Observable.create(FallbackReverseGeocoderObservable(coordinates.latitude, coordinates.longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            val place = getPlaceLocationModel(it, coordinates)
                            setLocation(place)

                        },
                        {
                            it.printStackTrace()
                            val place = PlaceLocationModel(coordinates.latitude, coordinates.longitude, "nope")
                            setLocation(place)
                        })
    }

    private fun getPlaceLocationModel(addresses: List<Address>, coordinates: LatLng): PlaceLocationModel {
        val fullAddress = StringBuffer()
        if (addresses.isNotNullOrEmpty()) {
            fullAddress.append(addresses[0].thoroughfare)
            if(addresses[0].featureName.isNotNullOrEmpty()){
                fullAddress.append(", ").append(addresses[0].featureName)
            }
        }
        return PlaceLocationModel(coordinates.latitude, coordinates.longitude, fullAddress.toString())
    }

}