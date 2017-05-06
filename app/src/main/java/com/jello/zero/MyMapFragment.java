package com.jello.zero;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by hoangphat1908 on 5/4/2017.
 */

public class MyMapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Marker positionMarker;
    private Circle positionCircle;
    protected ArrayList<Alert> alertList = new ArrayList<>();
    protected ArrayList<Alert> feedList = new ArrayList<>();
    private ArrayList<Marker> alertMarkers = new ArrayList<>();
    private ArrayList<Marker> feedMarkers = new ArrayList<>();
    protected GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    protected Location mLastLocation;
    GoogleMap mGoogleMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private boolean zoom = true;
    public MyMapFragment() {
        super();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this.getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this.getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static MyMapFragment newInstance(ArrayList<Alert> alertList, ArrayList<Alert> feedList){
        MyMapFragment frag = new MyMapFragment();
        frag.alertList = alertList;
        frag.feedList = feedList;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);
        getMapAsync(this);


        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        UiSettings settings = mGoogleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


        //settings.setAllGesturesEnabled(false);
        //settings.setMyLocationButtonEnabled(false);
        for(Alert alert : alertList){
            Marker aMarker =   mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(alert.getLatitude(), alert.getLongitude()))
                        .title(alert.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(getMarkerStyle(alert.confirmed))));
            aMarker.setTag(alert);
            alertMarkers.add(aMarker);

        }
        for(Alert alert : feedList){
            Marker aMarker =   mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(alert.getLatitude(), alert.getLongitude()))
                    .title(alert.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerStyle(alert.confirmed))).visible(false));
            aMarker.showInfoWindow();
            aMarker.setTag(alert);
            feedMarkers.add(aMarker);

        }
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!marker.equals(positionMarker)) {
                    Alert value = (Alert) marker.getTag();
                    Intent intent = new Intent(getActivity(), ViewAlertActivity.class);
                    intent.putExtra("alert", value);
                    startActivity(intent);
                }

            }
        });
    }

    public float getMarkerStyle(int confirms){
        if(confirms <= 6)
            return BitmapDescriptorFactory.HUE_YELLOW;
        else if(confirms <= 12)
            return  BitmapDescriptorFactory.HUE_ORANGE;
        else return BitmapDescriptorFactory.HUE_RED;

    }
    public void includeAlerts(){
        for (Marker aMarker : alertMarkers)
            aMarker.setVisible(true);
    }
    public void excludeAlerts(){
        for (Marker aMarker : alertMarkers)
            aMarker.setVisible(false);
    }
    public void includeFeeds(){
        for (Marker aMarker : feedMarkers)
            aMarker.setVisible(true);
    }
    public void excludeFeeds(){
        for (Marker aMarker : feedMarkers)
            aMarker.setVisible(false);
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (positionMarker != null)
            positionMarker.remove();
        if(positionCircle != null)
            positionCircle.remove();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        positionMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        positionCircle = mGoogleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1000)
                .fillColor(0x3300FFFF).strokeWidth(0));

        //move map camera
        if(zoom) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            zoom = false;
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MyMapFragment.this.getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
}
