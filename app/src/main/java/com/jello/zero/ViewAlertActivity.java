package com.jello.zero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by hoangphat1908 on 3/19/2017.
 */

public class ViewAlertActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private DatabaseReference alertRef;
    private FirebaseDatabase database;
    private String key="";
    private LatLng location = new LatLng(0,0);
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = extras.getString("key");
        }
        database = FirebaseDatabase.getInstance();
        alertRef = database.getReference("alerts").child(key);
        final TextView alertTitle = (TextView)findViewById(R.id.alertViewTitle);
        final TextView alertCategory = (TextView)findViewById(R.id.alertViewCategory);
        final TextView alertLocation = (TextView)findViewById(R.id.alertViewLocation);
        alertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alert theAlert = dataSnapshot.getValue(Alert.class);
                alertTitle.setText(theAlert.name);
                alertCategory.setText(theAlert.category);
                //Temporary location
                alertLocation.setText("Location: "+theAlert.latitude+","+theAlert.longitude);
                setLoc(theAlert.latitude,theAlert.longitude);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(location).title("Marker in Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));
    }
    public void setLoc(double latitude, double longitude)
    {
        this.location = new LatLng(latitude,longitude);
    }

}


