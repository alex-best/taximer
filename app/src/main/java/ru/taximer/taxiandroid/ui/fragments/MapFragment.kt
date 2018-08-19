package ru.taximer.taxiandroid.ui.fragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.main_container
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.ui.FallbackReverseGeocoderObservable
import ru.taximer.taxiandroid.utils.PERMISSIONS_LOCATION
import ru.taximer.taxiandroid.utils.arePermissionsGranted
import ru.taximer.taxiandroid.utils.isNotNullOrEmpty
import ru.taximer.taxiandroid.utils.shouldShowRationale
import java.io.IOException
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var map: GoogleMap? = null
    var currentMarker: Marker? = null

    private var locationProvider: FusedLocationProviderClient? = null
    private var addressDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
            activity?.let { it ->
                locationProvider = LocationServices.getFusedLocationProviderClient(it)
                locationProvider?.lastLocation?.addOnSuccessListener {
                    onLocationChanged(it)
                }
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
        val gps = LatLng(location.latitude, location.longitude)
        map?.let {
            detectAddress(gps)
            if (currentMarker != null) {
                currentMarker?.position = gps
            }
            else {
                val icnGenerator = IconGenerator(activity!!)
                val iconBitmap = icnGenerator.makeIcon("Position")
                it.addMarker(MarkerOptions()
                        .position(gps)
                        .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)))
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, DEFAULT_ZOOM))
            }
        }

    }

    private fun detectAddress(coordinates: LatLng) {
        activity ?: return

        addressDisposable?.dispose()

        addressDisposable = Observable.fromCallable<PlaceLocationModel> {
            val geocoder = Geocoder(activity!!, Locale.getDefault())
            val addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
            getPlaceLocationModel(addresses, coordinates)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if(activity != null && currentMarker != null) {
                                val icnGenerator = IconGenerator(activity!!)
                                val iconBitmap = icnGenerator.makeIcon(it.address)
                                currentMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                                Log.d("Dto", it.address)
                            }

                        },
                        { detectFallbackReverseGeocode(coordinates) })
    }

    private fun detectFallbackReverseGeocode(coordinates: LatLng) {
        addressDisposable?.dispose()

        addressDisposable = Observable.create(FallbackReverseGeocoderObservable(coordinates.latitude, coordinates.longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            val place = getPlaceLocationModel(it, coordinates)
                            if(activity != null && currentMarker != null) {
                                val icnGenerator = IconGenerator(activity!!)
                                val iconBitmap = icnGenerator.makeIcon(place.address)
                                currentMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                                Log.d("Dto", place.address)
                            }

                        },
                        {
                            it.printStackTrace()
                            val place = PlaceLocationModel(coordinates.latitude, coordinates.longitude, "nope")
                            if(activity != null && currentMarker != null) {
                                val icnGenerator = IconGenerator(activity!!)
                                val iconBitmap = icnGenerator.makeIcon(place.address)
                                currentMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                                Log.d("Dto", place.address)
                            }
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

    companion object {

        private const val PERMISSION_REQUEST_CODE_LOCATION = 7
        private const val DEFAULT_ZOOM = 12f
    }
}