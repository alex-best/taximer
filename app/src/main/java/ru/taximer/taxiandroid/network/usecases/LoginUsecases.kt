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
}


///////////////////////////////////////////////////////////////////////////
// User Usecases Impl
///////////////////////////////////////////////////////////////////////////

class UserUsecasesImpl(
        private val endpoints: TaxiApi
) : UserUsecases {

    override fun openApp(device_hash: String): Flowable<Unit> =
            endpoints.authUser(
                    device_hash,
                    Config.device_type
            )
                    .map {
                        if(it.success) {
                            Prefs.storeToken(it.result.user.api_token ?: "")
                            Unit
                        }else{
                            throw ResponseException(it.errors)
                        }
                    }.applyDefaultNetSchedulers()
}