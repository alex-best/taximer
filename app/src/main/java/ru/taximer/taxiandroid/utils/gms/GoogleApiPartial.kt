package ru.taximer.taxiandroid.utils.gms

import android.app.Dialog
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialogFragment
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.GoogleApiClient
import java.lang.ref.WeakReference

///////////////////////////////////////////////////////////////////////////
// Google Api Partial Activity Callbacks
///////////////////////////////////////////////////////////////////////////

interface GoogleApiPartialActivityCallbacks {
    fun onGoogleApiClientErrorResolved()
}


///////////////////////////////////////////////////////////////////////////
// Google Api Partial Callbacks
///////////////////////////////////////////////////////////////////////////

private const val TAG = "GoogleApiPartial"

interface GoogleApiPartialCallbacks{

    fun onGoogleApiClientConnected() {
        Log.w (TAG, "Google Api Partial connected")
    }

    fun onGoogleApiClientConnectionSuspended(cause: Int) {
        Log.w (TAG,
            "Google Api Partial disconnected coz ${when (cause) {
                GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST -> "network lost"
                GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED -> "service disconnected"
                else -> "nobody knows"
            }}"
        )
    }

    fun onGoogleApiClientConnectionFailed() {
        Log.w (TAG,"Google Api Partial connection failed" )
    }

    fun onGoogleApiClientErrorResolved()
}


///////////////////////////////////////////////////////////////////////////
// Google Api Partial
///////////////////////////////////////////////////////////////////////////

private const val CONNECTION_ERROR_CODE = "google_api_client_connection_error_code"
private const val REQUEST_RESOLVE_ERROR = 1212

class GoogleApiPartial<Act>(
        activity: Act,
        callbacks: GoogleApiPartialCallbacks,
        vararg apis: Api<out Api.ApiOptions.NotRequiredOptions>
) :
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener
where Act : AppCompatActivity, Act : GoogleApiPartialActivityCallbacks {

    private val OUT_STATE_RESOLVING_ERROR = "RESOLVING_ERROR_OUT_STATE"

    private val TAG_DIALOG_ERROR = "google_api_client_error_dialog_tag"

    private val activity = WeakReference<AppCompatActivity?>(activity)
    private val callbacks = WeakReference<GoogleApiPartialCallbacks?>(callbacks)
    internal var googleApiClient: GoogleApiClient?
        private set

    private var isResolvingError = false

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    init {
        val apiBuilder = GoogleApiClient.Builder(activity.applicationContext, this, this)
        for (api in apis) {
            apiBuilder.addApi(api)
        }

        googleApiClient = apiBuilder.build()
    }

    fun restoreState(savedState: Bundle?) {
        if (savedState != null) {
            isResolvingError = savedState.getBoolean(OUT_STATE_RESOLVING_ERROR, false)
        }
    }

    fun start() {
        googleApiClient?.connect()
    }

    fun saveState(outState: Bundle) {
        outState.putBoolean(OUT_STATE_RESOLVING_ERROR, isResolvingError)
    }

    fun reconnect() {
        googleApiClient?.reconnect()
    }

    fun stop() {
        googleApiClient?.disconnect()
    }

    fun onErrorResolved() {
        isResolvingError = false
    }

    fun destroy() {
        googleApiClient = null
        activity.clear()
        callbacks.clear()
    }

    ///////////////////////////////////////////////////////////////////////////
    // State
    ///////////////////////////////////////////////////////////////////////////

    val isConnected: Boolean
        get() = googleApiClient?.isConnected ?: false

    val isConnecting: Boolean
        get() = googleApiClient?.isConnecting ?: false

    ///////////////////////////////////////////////////////////////////////////
    // Google Api Client Callbacks
    ///////////////////////////////////////////////////////////////////////////

    override fun onConnected(p0: Bundle?) {
        callbacks.get()?.onGoogleApiClientConnected()
    }

    override fun onConnectionSuspended(cause: Int) {
        callbacks.get()?.onGoogleApiClientConnectionSuspended(cause)
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        if (isResolvingError) return

        val activity = activity.get() ?: return
        isResolvingError = true
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR)
            }
            catch (e: IntentSender.SendIntentException) {
                // smth went terribly wrong. trying again
                error(e)
                isResolvingError = false
                googleApiClient?.connect()
            }
        }
        else {
            val args = Bundle()
            args.putInt(CONNECTION_ERROR_CODE, result.errorCode)

            val dialogFragment = ErrorDialogFragment()
            dialogFragment.arguments = args
            dialogFragment.show(activity.supportFragmentManager, TAG_DIALOG_ERROR)
        }

        callbacks.get()?.onGoogleApiClientConnectionFailed()
    }

    class ErrorDialogFragment : AppCompatDialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val errorCode = arguments?.getInt(CONNECTION_ERROR_CODE) ?: -1
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    activity!!, errorCode, REQUEST_RESOLVE_ERROR
            )
        }

        override fun onDismiss(dialog: DialogInterface?) {
            super.onDismiss(dialog)
            (activity as? GoogleApiPartialActivityCallbacks)?.onGoogleApiClientErrorResolved()
        }
    }
}
