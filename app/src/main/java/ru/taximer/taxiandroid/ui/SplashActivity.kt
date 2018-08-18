package ru.taximer.taxiandroid.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.main_container
import kotlinx.android.synthetic.main.activity_splash.root_container
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.utils.PERMISSIONS_LOCATION
import ru.taximer.taxiandroid.utils.arePermissionsGranted
import ru.taximer.taxiandroid.utils.shouldShowRationale

class SplashActivity : AppCompatActivity() {

    private var locationProvider: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        checkMyLocationPermission {
            locationProvider = LocationServices.getFusedLocationProviderClient(this)
            locationProvider?.lastLocation?.addOnSuccessListener {
                Prefs.storeGeo(LatLng(it.latitude, it.longitude))
                MainTaxiScreen.launch(this)
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

    companion object {
        private const val PERMISSION_REQUEST_CODE_LOCATION = 7
    }
}
