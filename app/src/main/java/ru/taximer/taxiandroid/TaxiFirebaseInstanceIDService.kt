package ru.taximer.taxiandroid

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import ru.taximer.taxiandroid.network.usecases.UserUsecases


class TaxiFirebaseInstanceIDService : FirebaseInstanceIdService() {
    val usecases: UserUsecases = TaximerApp.appComponent.baseUsecases()

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token

        refreshedToken ?: return

        usecases.sendPush(refreshedToken).subscribe()
    }
}