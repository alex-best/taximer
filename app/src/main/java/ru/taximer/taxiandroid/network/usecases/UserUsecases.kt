package ru.taximer.taxiandroid.network.usecases

import io.reactivex.Flowable
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.network.Config
import ru.taximer.taxiandroid.network.TaxiApi
import ru.taximer.taxiandroid.network.models.ResponseException


///////////////////////////////////////////////////////////////////////////
// User Usecases
///////////////////////////////////////////////////////////////////////////

interface UserUsecases {
    fun openApp(device_hash: String): Flowable<Unit>
    fun sendPush(token: String): Flowable<Unit>
    fun setNotifications(value: Boolean): Flowable<Boolean>
}


///////////////////////////////////////////////////////////////////////////
// User Usecases Impl
///////////////////////////////////////////////////////////////////////////

class UserUsecasesImpl(
        private val endpoints: TaxiApi
) : UserUsecases {

    override fun setNotifications(value: Boolean): Flowable<Boolean> =
            endpoints.changeNotifications(value)
                    .map {
                        value
                    }.applyDefaultNetSchedulers()

    override fun openApp(device_hash: String): Flowable<Unit> =
            endpoints.authUser(
                    device_hash,
                    Config.device_type
            )
                    .map {
                        if(it.success) {
                            Prefs.storeToken(it.result?.user?.api_token ?: "")
                            Unit
                        }else{
                            throw ResponseException(it.errors[0])
                        }
                    }.applyDefaultNetSchedulers()

    override fun sendPush(token: String): Flowable<Unit> =
            endpoints.sendPush(token)
                    .map {
                        Unit
                    }
                    .applyDefaultNetSchedulers()
}