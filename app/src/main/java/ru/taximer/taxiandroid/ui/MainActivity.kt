package ru.taximer.taxiandroid.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationListener
import android.location.LocationManager
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.RelativeLayout.LayoutParams
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.main_container
import kotlinx.android.synthetic.main.activity_main.mapView
import kotlinx.android.synthetic.main.custom_marker.markerText
import kotlinx.android.synthetic.main.search_location_sheeet.autocompleteAddresses
import kotlinx.android.synthetic.main.search_location_sheeet.tmpBar

import java.io.IOException
import java.util.Locale

import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.Config
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.utils.PERMISSIONS_LOCATION
import ru.taximer.taxiandroid.utils.arePermissionsGranted
import ru.taximer.taxiandroid.utils.isNotNullOrEmpty
import ru.taximer.taxiandroid.utils.shouldShowRationale

class MainActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    var map: GoogleMap? = null
    var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    var currentMarker: Marker? = null


    private var locationProvider: FusedLocationProviderClient? = null
    private var addressDisposable: Disposable? = null
    private var isDetectedPlace = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val sheetView = autocompleteAddresses.parent.parent as FrameLayout
        mBottomSheetBehavior = BottomSheetBehavior.from(sheetView)
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        mBottomSheetBehavior!!.peekHeight = 130
        tmpBar.requestLayout()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onBackPressed() {
        if (mBottomSheetBehavior!!.state != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        addressDisposable?.dispose()
        addressDisposable = null

        super.onDestroy()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Map stuff
    ///////////////////////////////////////////////////////////////////////////

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.isIndoorEnabled = true
        checkMyLocationPermissionSilent {
            allowMyLocation()
        }
        getCurrentLocation()

        map?.moveCamera(CameraUpdateFactory.zoomTo(11f))
    }

    @SuppressLint("MissingPermission")
    private fun allowMyLocation() {
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
    }

    private fun getCurrentLocation() {
        checkMyLocationPermission {
            locationProvider = LocationServices.getFusedLocationProviderClient(this)
            locationProvider?.lastLocation?.addOnSuccessListener {
                onLocationChanged(it)
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

    private fun checkMyLocationPermissionSilent(func: () -> Unit) {
        if (arePermissionsGranted(*PERMISSIONS_LOCATION)) func()
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
                    allowMyLocation()
                    getCurrentLocation()
                }
                else {
                    val snackbar = Snackbar.make(main_container, getText(R.string.rationale_location_permission), Snackbar.LENGTH_LONG)
                    snackbar.setAction(R.string.label_ok) {
                        requestLocationPermission()
                    }
                    snackbar.show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Location change listener
    ///////////////////////////////////////////////////////////////////////////

    override fun onLocationChanged(location: Location?) {
        location ?: return
        drawMarker(location)
    }

    @Throws(IOException::class)
    private fun drawMarker(location: Location?) {
        location ?: return
        val gps = LatLng(location.longitude, location.latitude)
        map?.let {
            detectAddress(gps)
            if(currentMarker != null){
                currentMarker?.position = gps
            }else{
                val marker = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_marker, null)
                it.addMarker(MarkerOptions()
                        .position(gps)
                        .title("Current Position")
                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))))
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, DEFAULT_ZOOM))
            }
        }

    }

    private fun detectAddress(coordinates: LatLng) {
        addressDisposable?.dispose()

        addressDisposable = Observable.fromCallable<PlaceLocationModel> {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
            getPlaceLocationModel(addresses, coordinates)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { markerText.text = it.address},
                        { detectFallbackReverseGeocode(coordinates) })
    }

    private fun detectFallbackReverseGeocode(coordinates: LatLng) {
        addressDisposable?.dispose()

        addressDisposable = Observable.create(FallbackReverseGeocoderObservable(coordinates.latitude, coordinates.longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            val place = getPlaceLocationModel(it, coordinates)
                            markerText.text = place.address

                        },
                        {
                            it.printStackTrace()
                            val place = PlaceLocationModel(coordinates.latitude, coordinates.longitude, "nope")
                            markerText.text = place.address
                        })
    }

    private fun getPlaceLocationModel(addresses: List<Address>, coordinates: LatLng): PlaceLocationModel {
        val fullAddress = StringBuffer()
        if (addresses.isNotNullOrEmpty()) {
            for (line in 0..addresses[0].maxAddressLineIndex) {
                fullAddress.append(addresses[0].getAddressLine(line) ?: "")
                if (line != addresses[0].maxAddressLineIndex) fullAddress.append(", ")
            }
        }
        return PlaceLocationModel(coordinates.latitude, coordinates.longitude, fullAddress.toString())
    }

    fun showSearchDialog() {
        val dialog = ChooseDirectionDialog()
        dialog.show(supportFragmentManager, "SEARCH_DIALOG_TAG")
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE_LOCATION = 7
        private const val DEFAULT_ZOOM = 12f

        fun createDrawableFromView(context: Context, view: View): Bitmap {
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            view.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)
            view.draw(canvas)

            return bitmap
        }
    }

}
