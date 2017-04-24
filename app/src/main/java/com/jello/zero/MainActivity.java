package com.jello.zero;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference alertRef = ref.child("alerts");
    private ArrayList<Alert> alertList = new ArrayList<>();
    private ListView listView;
    ChildEventListener alertListener;
    private ArrayAdapter<Alert> theAdapter;

    Location mCurrentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    notLoggedIn();
                }
            }
        };
    }
/*
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleAPiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

*/
    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);

        listView = (ListView)findViewById(R.id.alertListView);
        theAdapter = new ArrayAdapter<Alert>(this,R.layout.alert_row,alertList);
        listView.setAdapter(theAdapter);
        alertListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey){
                Alert newAlert = dataSnapshot.getValue(Alert.class);
                String distance;

                if(mCurrentLocation != null){
                    Location alertLocation = new Location("");
                    alertLocation.setLatitude(newAlert.latitude);
                    alertLocation.setLongitude(newAlert.longitude);
                    distance = Math.round(alertLocation.distanceTo(mCurrentLocation)) + " meters away from you!";
                }else{
                    distance = "Distance away from you cannot be detected.";
                }
                newAlert.setKey(dataSnapshot.getKey());
                newAlert.setDistance(distance);
                String text = newAlert.toString();
                alertList.add(newAlert);
                theAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        alertRef.addChildEventListener(alertListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Alert value = (Alert)adapter.getItemAtPosition(position);
                System.out.print(value.name);
                Intent intent = new Intent(MainActivity.this, ViewAlertActivity.class);
                intent.putExtra("key", value.getKey());
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStop(){
        super.onStop();
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
        alertRef.removeEventListener(alertListener);
        alertList.clear();
        theAdapter.notifyDataSetChanged();
    }

    public void notLoggedIn(){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void switchToCreateAlert(View view){
        Intent intent = new Intent(this, CreateAlertActivity.class);
        startActivity(intent);
    }

    public void signoutUser(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
