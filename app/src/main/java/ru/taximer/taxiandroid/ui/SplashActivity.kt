package ru.taximer.taxiandroid.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.util.Log
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.main_container
import kotlinx.android.synthetic.main.activity_splash.gifImage
import kotlinx.android.synthetic.main.activity_splash.root_container
import pl.droidsonroids.gif.GifDrawable
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.presenters.SplashPresenter
import ru.taximer.taxiandroid.presenters.SplashView
import ru.taximer.taxiandroid.utils.PERMISSIONS_LOCATION
import ru.taximer.taxiandroid.utils.arePermissionsGranted
import ru.taximer.taxiandroid.utils.shouldShowRationale

class SplashActivity : MvpAppCompatActivity(), SplashView {

    @InjectPresenter(type = PresenterType.LOCAL)
    lateinit var presenter: SplashPresenter

    @ProvidePresenter(type = PresenterType.LOCAL)
    fun provideSplashPresenter(): SplashPresenter = SplashPresenter()

    var duration: Long = 0L

    private var locationProvider: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val drawable = GifDrawable.createFromResource(resources, R.drawable.app_icon_final)
        duration = drawable?.duration?.toLong() ?: 0L
        gifImage.setImageDrawable(drawable)
        drawable?.start()
        getCurrentLocation()
        Prefs.clearSettings()
    }

    @SuppressLint("HardwareIds")
    private fun getCurrentLocation() {
        checkMyLocationPermission {
            locationProvider = LocationServices.getFusedLocationProviderClient(this)
            locationProvider?.lastLocation?.addOnSuccessListener {
                Prefs.storeGeo(LatLng(it?.latitude ?: 0.0, it?.longitude ?: 0.0))
                presenter.openApp(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Location permission flow
    ///////////////////////////////////////////////////////////////////////////

    private fun checkMyLocationPermission(func: () -> Unit) {
        if (arePermissionsGranted(*PERMISSIONS_LOCATION)) func()
        else showLocationRationale()
    }

    private fun showLocationRationale() {
        if (shouldShowRationale(*PERMISSIONS_LOCATION)) {
            val snackbar = Snackbar.make(main_container, getText(R.string.rationale_location_permission), Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.label_ok) {
                requestLocationPermission()
            }
            snackbar.show()
        }
        else requestLocationPermission()
    }

    private fun requestLocationPermission() {
        requestPermissions(
                PERMISSIONS_LOCATION,
                PERMISSION_REQUEST_CODE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                if (arePermissionsGranted(*PERMISSIONS_LOCATION)) {
                    getCurrentLocation()
                }
                else {
                    val snackbar = Snackbar.make(root_container, getText(R.string.rationale_location_permission), Snackbar.LENGTH_LONG)
                    snackbar.setAction(R.string.label_ok) {
                        requestLocationPermission()
                    }
                    snackbar.show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun hideLoading() {
        //nope
    }

    override fun showLoading() {
        //nope
    }

    override fun goToMainScreen() {

        val handler = Handler()
        handler.postDelayed({
            MainTaxiScreen.launch(this)
            finish()
        }, duration)
    }

    override fun showError(message: String) {
        Log.d("Dto", message)
        val snackbar = Snackbar.make(root_container, message, Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE_LOCATION = 7
    }
}
