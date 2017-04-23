package com.jello.zero;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference alertRef = ref.child("alerts");
    private ArrayList<Alert> alertList = new ArrayList<>();
    private ListView listView;
    ChildEventListener alertListener;
    private ArrayAdapter<Alert> theAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public MainFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


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

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Button buttonToCreateAlert = (Button) rootView.findViewById(R.id.createAlertButton);
        Button buttonToSignOut = (Button) rootView.findViewById(R.id.signOutButton);

        buttonToCreateAlert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainFragment.this.getActivity(), CreateAlertActivity.class);
                startActivity(intent);
            }
        });

        buttonToSignOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });





        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
        auth.addAuthStateListener(authListener);

        listView = (ListView)getView().findViewById(R.id.alertListView);
        theAdapter = new ArrayAdapter<Alert>(this.getActivity(),R.layout.alert_row,alertList);
        listView.setAdapter(theAdapter);
        alertListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey){
                Alert newAlert = dataSnapshot.getValue(Alert.class);
                String distance;

                if(mLastLocation != null){
                    Location alertLocation = new Location("");
                    alertLocation.setLatitude(newAlert.latitude);
                    alertLocation.setLongitude(newAlert.longitude);
                    distance = Math.round(alertLocation.distanceTo(mLastLocation)) + " meters away from you!";
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
                Intent intent = new Intent(MainFragment.this.getActivity(), ViewAlertActivity.class);
                intent.putExtra("key", value.getKey());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
        alertRef.removeEventListener(alertListener);
        alertList.clear();
        theAdapter.notifyDataSetChanged();
    }

    public void notLoggedIn(){
        Intent intent = new Intent(this.getActivity(), CreateAccountActivity.class);
        startActivity(intent);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

