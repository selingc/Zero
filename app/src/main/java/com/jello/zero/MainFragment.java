package com.jello.zero;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public abstract class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String TAG = "MainActivity";
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference alertRef = ref.child("alerts");
    protected ArrayList<Alert> alertList = new ArrayList<>();
    protected ListView listView;
    ChildEventListener alertListener;
    protected ArrayAdapter<Alert> theAdapter;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerApdapter recyclerApdapter;

    public MainFragment() {
    }

    public abstract MainFragment newInstance(int sectionNumber);
    public abstract String getAlertReference();
    public abstract Alert retrieveAlert(DataSnapshot snapshot);
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

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        alertRef = ref.child(getAlertReference());
        mGoogleApiClient.connect();
        auth.addAuthStateListener(authListener);


        //  listView = (ListView)getView().findViewById(R.id.alertListView);
       // theAdapter = new ArrayAdapter<Alert>(this.getActivity(),R.layout.alert_row,alertList);
       // listView.setAdapter(theAdapter);

        mRecyclerView = (RecyclerView)getView().findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerApdapter = new RecyclerApdapter(alertList);
        mRecyclerView.setAdapter(recyclerApdapter);

        //add item divider
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);


        alertListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey){

                Alert newAlert = retrieveAlert(dataSnapshot);

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
                recyclerApdapter.notifyDataSetChanged();
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


        /*
        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                Alert value = (Alert)recyclerApdapter.getItemAtPosition(position);
                Intent intent = new Intent(MainFragment.this.getActivity(), ViewAlertActivity.class);
                intent.putExtra("alert", value);
                startActivity(intent);
            }
        });*/
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
        recyclerApdapter.notifyDataSetChanged();
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

