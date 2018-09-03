package ru.taximer.taxiandroid.utils.gms

import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable


///////////////////////////////////////////////////////////////////////////
// Google Places Provider
///////////////////////////////////////////////////////////////////////////

fun GoogleApiPartial<*>.getPredictions(query: String, bounds: LatLngBounds?, autocompleteFilter: AutocompleteFilter?): Flowable<AutocompletePredictionBuffer>? {
    val googleClient = googleApiClient
    return if (googleClient != null && googleClient.isConnected && googleClient.hasConnectedApi(Places.GEO_DATA_API))
        Flowable.create({ emitter ->
            val result = Places.GeoDataApi.getAutocompletePredictions(
                    googleClient,
                    query,
                    bounds,
                    autocompleteFilter
            )
            result.setResultCallback {
                emitter.onNext(it)
                emitter.onComplete()
            }
        }, BackpressureStrategy.BUFFER)
    else null
}

fun GoogleApiPartial<*>.getPlace(placeId: String): Flowable<PlaceBuffer>? {
    val googleClient = googleApiClient
    return if (googleClient != null && googleClient.isConnected && googleClient.hasConnectedApi(Places.GEO_DATA_API))
        Flowable.create({ emitter ->
            val result = Places.GeoDataApi.getPlaceById(googleClient,
                    placeId)
            result.setResultCallback {
                emitter.onNext(it)
                emitter.onComplete()
            }

        }, BackpressureStrategy.BUFFER)
    else null
}