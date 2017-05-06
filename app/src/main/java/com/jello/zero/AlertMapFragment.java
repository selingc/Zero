package com.jello.zero;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by hoangphat1908 on 5/4/2017.
 */

public class AlertMapFragment extends Fragment{
    private View rootView;
    private LatLng location = new LatLng(0,0);
    private GoogleMap googleMap;
    private static final String TAG = "MainActivity";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference alertRef = ref.child("alerts");
    DatabaseReference feedRef = ref.child("feed");
    ChildEventListener alertListener;
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;

    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected ArrayList<Alert> alertList = new ArrayList<>();
    protected ArrayList<Alert> feedList = new ArrayList<>();
    MyMapFragment mapFragment;


    public AlertMapFragment(){

    }
    public AlertMapFragment newInstance(int sectionNumber) {
        AlertMapFragment fragment = new AlertMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alert_map, container, false);
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




        alertListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey){
                Alert newAlert = dataSnapshot.getValue(Alert.class);
                alertList.add(newAlert);
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

        alertListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey){
                Alert newAlert = new Alert(
                        dataSnapshot.child("title").getValue(String.class),
                        dataSnapshot.child("description").getValue(String.class),
                        dataSnapshot.child("location").getValue(String.class),
                        dataSnapshot.child("latitude").getValue(String.class),
                        dataSnapshot.child("longtitude").getValue(String.class),
                        "",
                        0
                );
                feedList.add(newAlert);
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
        feedRef.addChildEventListener(alertListener);

        CheckBox alertCheck=(CheckBox)rootView.findViewById(R.id.alertCheck);
        CheckBox feedCheck=(CheckBox)rootView.findViewById(R.id.feedCheck);
        alertCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            boolean checked = ((CheckBox) v).isChecked();
                if(checked)
                    mapFragment.includeAlerts();
                else
                    mapFragment.excludeAlerts();

            }
        });
        feedCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if(checked)
                    mapFragment.includeFeeds();
                else
                    mapFragment.excludeFeeds();

            }
        });




        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (MyMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = MyMapFragment.newInstance(this.alertList, this.feedList);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
    }


    @Override
    public void onStart() {
        super.onStart();


    }
    @Override
    public void onStop(){
        super.onStop();

        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }


    public void notLoggedIn(){
        Intent intent = new Intent(this.getActivity(), CreateAccountActivity.class);
        startActivity(intent);
    }
    public void setLoc(double latitude, double longitude) {
        this.location = new LatLng(latitude, longitude);
    }




}
