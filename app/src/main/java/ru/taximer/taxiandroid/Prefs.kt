package ru.taximer.taxiandroid

import com.google.android.gms.maps.model.LatLng
import com.orhanobut.hawk.Hawk

object Prefs {
    private val GEO_LAT = "geo_lat"
    private val GEO_LON = "geo_lon"
    private val TOKEN = "token"

    fun storeGeo(geo: LatLng) {
        Hawk.put(GEO_LAT, geo.latitude)
        Hawk.put(GEO_LON, geo.longitude)
    }

    fun readGeo(): LatLng {
        return LatLng(Hawk.get(GEO_LAT, 0.0), Hawk.get(GEO_LON, 0.0))
    }

    fun storeToken(token: String) = Hawk.put(TOKEN, token)
    fun getToken() = Hawk.get(TOKEN, "")
}
