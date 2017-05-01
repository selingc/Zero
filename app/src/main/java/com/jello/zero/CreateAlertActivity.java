package com.jello.zero;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangphat1908 on 3/5/2017.
 */

public class CreateAlertActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private static final String TAG = "CreateAlert";
    private DatabaseReference alertsRef;
    private Location mLastLocation = null;
    private AddressResultReceiver mResultReceiver;
    private double longitude;
    private double latitude;
    private String name;
    private String category;
    private String location;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);
        database = FirebaseDatabase.getInstance();
        alertsRef = database.getReference("alerts");
        Log.d(TAG, "onCreate");
        mResultReceiver = new AddressResultReceiver(null);

        if (mGoogleApiClient == null) {
            Log.d(TAG, "OnCreate try to get api client");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Log.d(TAG, "onConnect");
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public void createAlert(View view) {
        name = ((EditText) findViewById(R.id.name_alert)).getText().toString();
        category = ((EditText) findViewById(R.id.category_alert)).getText().toString();
        location = ((EditText) findViewById(R.id.location_alert)).getText().toString();
        Log.d(TAG, "creating alert");
        //start intent here
        getCurrentCoordinatesBaseOnLoc(location);


        //addAlert(name, category, location, latitude, longitude);
        //alertCreated();

    }

    public void addAlert(String name, String category, String location, double latitude, double longitude) {
        Log.d(TAG, "add alert");
        Alert newAlert;
        if(mLastLocation == null){
            newAlert = new Alert(name, category, location, latitude, longitude, "", 0);
            Log.d(TAG, "addAlert mLastLocation null");
        }else{
            newAlert = new Alert(name, category, location, mLastLocation.getLatitude(), mLastLocation.getLongitude(), "", 0);
            Log.d(TAG, "addAlert location not null "+mLastLocation.getLatitude()+", "+mLastLocation.getLongitude());
        }

        DatabaseReference newAlertRef = alertsRef.push();
        //set key for alert
        String key = newAlertRef.getKey();
        newAlert.setKey(key);
        newAlertRef.setValue(newAlert);
    }

    public void alertCreated() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void switchToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
            }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String latitude = String.valueOf(mLastLocation.getLatitude());
            String longitude = String.valueOf(mLastLocation.getLongitude());
            ((EditText) findViewById(R.id.location_alert)).setHint("Type or use current location: " + latitude + "," + longitude);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getCurrentCoordinatesBaseOnLoc(String location){
        Intent intent = new Intent(this, GeocodeIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        //you always need these 2
        if(location.length()==0){ //do this if you want to pass in mlastlocation
            intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.COORDINATE); //type of input coordinate or name
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation); // pass in coordinate if choose coordinate
            Log.d(TAG, "get location by current location "+ mLastLocation);
        }else{ //or this if you want to pass in name of location
            intent.putExtra(Constants.FETCH_TYPE_EXTRA, Constants.ADDRESS);
            intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, location);  //pass in name if choose name
            Log.d(TAG, "get location by name");
        }

        Log.d(TAG, "starting service");
        startService(intent);  //start intent, it's gonna run in background
    }

//define this receiver class in the file you're working on
    class AddressResultReceiver extends ResultReceiver{
        public AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) { //when the thing is done, result is passed back here
                final Address address = resultData.getParcelable(Constants.RESULT_DATA); //this retrieve the address
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {  //this part is where you put whatever you want to do
                        Log.d(TAG, "on receive result");
                        longitude = address.getLongitude();
                        latitude = address.getLatitude();
                        location = resultData.getString(Constants.RESULT_DATA_KEY);
                        Log.d(TAG, address.getAdminArea()+","+address.getSubLocality()+","+address.getSubAdminArea());
                        Log.d(TAG, "on receive result "+longitude+", "+latitude+", "+location);
                        addAlert(name, category, location, latitude, longitude);
                        alertCreated();
                    }
                });
            }else{
                Log.d(TAG, "Unable to find longitude latitude");
            }
        }
    }
}
