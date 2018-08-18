package ru.taximer.taxiandroid.network.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place


///////////////////////////////////////////////////////////////////////////
// Place Location Model
///////////////////////////////////////////////////////////////////////////

data class PlaceLocationModel(val latitude: Double, val longitude: Double, val address: String) : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<PlaceLocationModel> {
            override fun createFromParcel(source: Parcel): PlaceLocationModel =
                    PlaceLocationModel(source)

            override fun newArray(size: Int): Array<PlaceLocationModel?> = arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString())

    constructor(place: Place) : this(place.latLng.latitude, place.latLng.longitude, place.address.toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }
}

val emptyPlaceLocation = PlaceLocationModel(0.0, 0.0, "")


///////////////////////////////////////////////////////////////////////////
// Place Prediction Model
///////////////////////////////////////////////////////////////////////////

class PlacePredictionModel : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<PlacePredictionModel> {
            override fun createFromParcel(source: Parcel): PlacePredictionModel =
                    PlacePredictionModel(source)

            override fun newArray(size: Int): Array<PlacePredictionModel?> = arrayOfNulls(size)
        }
    }

    val placeId: String
    val address: String
    val locality: String
    val fullAddress: String

    private constructor(parcelIn: Parcel) {
        placeId = parcelIn.readString()
        address = parcelIn.readString()
        locality = parcelIn.readString()
        fullAddress = parcelIn.readString()
    }

    constructor(autocompletePrediction: AutocompletePrediction) {
        placeId = autocompletePrediction.placeId ?: ""
        address = autocompletePrediction.getPrimaryText(null)?.toString() ?: ""
        locality = trimToCity(autocompletePrediction.getSecondaryText(null)?.toString() ?: "")
        fullAddress = autocompletePrediction.getFullText(null)?.toString() ?: ""
    }

    constructor(placeId: String = "", primaryText: String = "", secondaryText: String = "", fullText: String = "") {
        this.placeId = placeId
        this.address = primaryText
        this.locality = secondaryText
        this.fullAddress = fullText
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(placeId)
        dest.writeString(address)
        dest.writeString(locality)
        dest.writeString(fullAddress)
    }

    override fun describeContents() = 0

    private fun trimToCity(locality: String): String {
        return locality.split(",")[0].trim()
    }
}

val emptyPrediction = PlacePredictionModel()