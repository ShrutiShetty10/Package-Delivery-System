package com.example.packagedeliverysystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    boolean isPermissionGranted;
    GoogleMap mGoogleMap;
    private int GPS_REQUEST_CODE = 9001;
    double sourceLat, sourceLng, destinationLat, destinationLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        Point s = (Point) intent.getSerializableExtra("source");
        Point d = (Point) intent.getSerializableExtra("destination");
        sourceLat = s.getX();
        sourceLng = s.getY();
        destinationLat = d.getX();
        destinationLng = d.getY();

        checkMyPermission();

        if(isPermissionGranted) {
//            if(isGPSenabled()) {
            SupportMapFragment supportFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportFragment.getMapAsync(this);
//            }
        }

//        initMap();
//
//        geoLocate();
    }

//    private void geoLocate() {
//
//        LatLng latLng = new LatLng(sourceLat, sourceLng);
//
//        goToLocation(latLng.latitude, latLng.longitude);
//
//        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
//        mGoogleMap.addMarker((new MarkerOptions().position(new LatLng(destinationLat, destinationLng))));
//    }
//
//    private void goToLocation(double latitude, double longitude) {
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
//        mGoogleMap.moveCamera(cameraUpdate);
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//    }
//
//    private void initMap() {
//        if(isPermissionGranted) {
//            if(isGPSenabled()) {
//                SupportMapFragment supportFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                supportFragment.getMapAsync(this);
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == GPS_REQUEST_CODE) {
//            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            if(providerEnabled) {
//                Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "GPS is not enabled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private boolean isGPSenabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        if(providerEnabled) {
//            return true;
//        } else {
//            AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("GPS Permission")
//                    .setMessage("GPS is required for this app to work. Please enable GPS")
//                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent, GPS_REQUEST_CODE);
//                    }))
//                    .setCancelable(false)
//                    .show();
//
//            return false;
//        }
//    }

    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);

        LatLng latLng = new LatLng(sourceLat, sourceLng);

        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        mGoogleMap.addMarker((new MarkerOptions().position(new LatLng(destinationLat, destinationLng))));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}