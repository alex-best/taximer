package ru.taximer.taxiandroid.ui;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.taximer.taxiandroid.R;
import ru.taximer.taxiandroid.network.Config;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.recyclerview)
    RecyclerView autocompleteAddresses;
    @BindView(R.id.drag_me)
    TextView tmp_bar;
//    @BindView(R.id.app_bar)
//    AppBarLayout appbar;
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    GoogleMap map;
    BottomSheetBehavior mBottomSheetBehavior;
    LocationManager locationManager;
    Geocoder geocoder;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Toast.makeText(getApplicationContext(), String.format("%f, %f", location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
                try {
                    drawMarker(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locationManager.removeUpdates(mLocationListener);
            } else {
                Toast.makeText(getApplicationContext(),"Location is null", Toast.LENGTH_SHORT).show();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        FrameLayout sheetView = (FrameLayout) autocompleteAddresses.getParent().getParent();
        mBottomSheetBehavior = BottomSheetBehavior.from(sheetView);
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }

        mBottomSheetBehavior.setPeekHeight(130);
        tmp_bar.requestLayout();

        try {
            getCurrentLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    //TODO перенести это все (с картой) в отдельный класс/презентер
    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException { //TODO добавить рантайм проверку
        map = googleMap;
        map.setIndoorEnabled(true);
        map.setMyLocationEnabled(true);
//        map.addMarker(new MarkerOptions().position(new LatLng(40.7143528, -74.0059731)));
//        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(40.7143528, -74.0059731)));
        map.moveCamera(CameraUpdateFactory.zoomTo(11));
    }

    private void drawMarker(Location location) throws IOException{
        if (map != null) {
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
            TextView  markerText = (TextView ) marker.findViewById(R.id.markerText);
            markerText.setText(address);
            map.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position")
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
        }

    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void getCurrentLocation() throws SecurityException, IOException {//TODO добавить рантайм проверку
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            Snackbar.make(mapView, R.string.error_location, Snackbar.LENGTH_INDEFINITE).show();
        else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        Config.LOCATION_UPDATE_MIN_TIME, Config.LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        Config.LOCATION_UPDATE_MIN_TIME, Config.LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            Toast.makeText(getApplicationContext(), String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
                    location.getLongitude()), Toast.LENGTH_SHORT).show();
            drawMarker(location);
        }
    }

    public void showSearchDialog() {
        ChooseDirectionDialog dialog = new ChooseDirectionDialog();
        dialog.show(getSupportFragmentManager(), "SEARCH_DIALOG_TAG");
    }

}
