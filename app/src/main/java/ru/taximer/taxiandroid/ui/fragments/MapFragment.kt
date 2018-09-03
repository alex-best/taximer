package ru.taximer.taxiandroid.ui.fragments

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
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
import kotlinx.android.synthetic.main.activity_main.main_container
import ru.taximer.taxiandroid.Prefs
import ru.taximer.taxiandroid.R
import ru.taximer.taxiandroid.network.models.PlaceLocationModel
import ru.taximer.taxiandroid.presenters.MainTaxiPresenter
import ru.taximer.taxiandroid.presenters.MainTaxiView
import ru.taximer.taxiandroid.utils.PERMISSIONS_LOCATION
import ru.taximer.taxiandroid.utils.arePermissionsGranted
import ru.taximer.taxiandroid.utils.shouldShowRationale
import java.io.IOException
import java.util.Locale

class MapFragment : MvpAppCompatFragment(), OnMapReadyCallback, LocationListener, MainTaxiView, GoogleMap.OnMapClickListener {

    @InjectPresenter(type = PresenterType.GLOBAL)
    lateinit var presenter: MainTaxiPresenter

    @ProvidePresenter(type = PresenterType.GLOBAL)
    fun provideMainTaxiPresenter(): MainTaxiPresenter = MainTaxiPresenter()

    private var map: GoogleMap? = null
    var startMarker: Marker? = null
    var endMarker: Marker? = null

    private var locationProvider: FusedLocationProviderClient? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Map stuff
    ///////////////////////////////////////////////////////////////////////////

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map ?: return
        val settings = map!!.uiSettings
        with(settings) {
            setAllGesturesEnabled(true)
            isCompassEnabled = false
            isIndoorLevelPickerEnabled = true
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
        }

        map?.setOnMapClickListener(this)

        presenter.detectAddress(Prefs.readGeo(), Geocoder(activity!!, Locale.getDefault()))

        checkMyLocationPermissionSilent {
            allowMyLocation()
        }
        getCurrentLocation()

        map?.moveCamera(CameraUpdateFactory.zoomTo(11f))
    }

    override fun onMapClick(p0: LatLng?) {
        p0 ?: return
        presenter.detectAddress(p0, Geocoder(activity!!, Locale.getDefault()))
    }

    @SuppressLint("MissingPermission")
    private fun allowMyLocation() {
        map?.isMyLocationEnabled = false
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
        presenter.editStart()
        presenter.detectAddress(LatLng(location.latitude, location.longitude), Geocoder(activity!!, Locale.getDefault()))
    }

    @Throws(IOException::class)
    private fun drawStartMarker(gps: PlaceLocationModel?) {
        gps ?: return
        map?.let {
            if (startMarker != null) startMarker?.remove()
            else {
                it.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(gps.latitude, gps.longitude),
                                DEFAULT_ZOOM))
            }
            val icnGenerator = IconGenerator(activity!!)
            val iconBitmap = icnGenerator.makeIcon(gps.address)
            startMarker = it.addMarker(MarkerOptions()
                    .position(LatLng(gps.latitude, gps.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)))

        }
    }

    override fun setEnd(gps: PlaceLocationModel?) {
        gps ?: return
        map?.let {
            if (endMarker != null) endMarker?.remove()
            else {
                it.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(gps.latitude, gps.longitude),
                                DEFAULT_ZOOM))
            }
            val icnGenerator = IconGenerator(activity!!)
            val iconBitmap = icnGenerator.makeIcon(gps.address)
            endMarker = it.addMarker(MarkerOptions()
                    .position(LatLng(gps.latitude, gps.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap)))

        }
    }

    override fun setStart(location: PlaceLocationModel?) {
        drawStartMarker(location)
    }

    override fun setStep() {
        //nope
    }

    override fun startSearch(start: PlaceLocationModel, end: PlaceLocationModel) {
        //nope
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE_LOCATION = 7
        private const val DEFAULT_ZOOM = 12f
    }
}