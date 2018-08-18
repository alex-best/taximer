package ru.taximer.taxiandroid

import com.google.android.gms.maps.model.LatLng
import com.orhanobut.hawk.Hawk

object Prefs {
    private val GEO = "geo"

    fun storeGeo(geo: LatLng) {
        Hawk.put(GEO, geo)
    }

    fun readGeo(): LatLng {
        return Hawk.get(GEO, LatLng(0.0,0.0))
    }
}
