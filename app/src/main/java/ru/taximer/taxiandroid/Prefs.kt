package ru.taximer.taxiandroid

import com.google.android.gms.maps.model.LatLng
import com.orhanobut.hawk.Hawk

object Prefs {
    private val GEO_LAT = "geo_lat"
    private val GEO_LON = "geo_lon"
    private val TOKEN = "token"

    private val CAR_CLASS = "car_class"
    private val IS_CHILD = "is_child"
    private val CARD_PAY = "card_pay"
    private val CASH_PAY = "cash_pay"

    fun storeGeo(geo: LatLng) {
        Hawk.put(GEO_LAT, geo.latitude)
        Hawk.put(GEO_LON, geo.longitude)
    }

    fun readGeo(): LatLng {
        return LatLng(Hawk.get(GEO_LAT, 0.0), Hawk.get(GEO_LON, 0.0))
    }

    fun storeToken(token: String) = Hawk.put(TOKEN, token)
    fun getToken() = Hawk.get(TOKEN, "")!!

    fun storeCarClass(car: Int) = Hawk.put(CAR_CLASS, car)
    fun getCarClass() = Hawk.get(CAR_CLASS, -1)

    fun setIsChild(value: Boolean) = Hawk.put(IS_CHILD, value)
    fun isChild() = Hawk.get(IS_CHILD, false)

    fun setIsCard(value: Boolean) = Hawk.put(CARD_PAY, value)
    fun isCard() = Hawk.get(CARD_PAY, false)

    fun setIsCash(value: Boolean) = Hawk.put(CASH_PAY, value)
    fun isCash() = Hawk.get(CASH_PAY, false)

    fun clearSettings(){
        storeCarClass(-1)
        setIsCash(false)
        setIsCard(false)
        setIsChild(false)
    }
}
