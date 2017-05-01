package com.jello.zero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hoangphat1908 on 3/19/2017.
 */

public class ViewAlertActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private LatLng location = new LatLng(0,0);
    private String TAG = "ViewAlertActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Alert theAlert;
    private ChildEventListener confirmlistener;
    private DatabaseReference confirmListReference;
    private DatabaseReference alertListReference;
    private List<String> confirmedUser;
    private TextView confirmNumberTextView = null;
    private boolean wasConfirmed = false;
    private Set<String> confirmUserList = new HashSet<String>();
    private String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private Button confirmButton = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);

        theAlert = (Alert) getIntent().getSerializableExtra("alert");
        setView();
        confirmButton = (Button)findViewById(R.id.viewAlert_confirm_button);

        //reference for theAlert and the confirm list
        confirmListReference = FirebaseDatabase.getInstance().getReference().child("confirm").child(theAlert.key);
        alertListReference = FirebaseDatabase.getInstance().getReference().child("alerts").child(theAlert.key);

        //get list of users who have confirmed theAlert
        confirmListReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot confirmSnapshot: dataSnapshot.getChildren()) {
                    confirmUserList.add(confirmSnapshot.getValue().toString());
                    Log.d(TAG, "snapshot val "+ confirmSnapshot.getValue().toString());
                }

                //if current user is in this list, set wasConfirmed to true
                if(confirmUserList.contains(currentUserEmail)) {
                    confirmButton.setText("Unconfirmed");
                    wasConfirmed = true;
                    Log.d(TAG,"" +  wasConfirmed + confirmButton.getText());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    public boolean confirmAlert(View view){
        //preparation for updating list of confirmed user
        Map<String, Object> updatedConfirmedList = new HashMap<String, Object>();
        List<String> usersList = new ArrayList<String>();

        if(wasConfirmed==false) {  //alert is not confirmed by this user
            //add to users list
            confirmUserList.add(currentUserEmail);
            usersList.addAll(confirmUserList);

            //increment alert's confirm
            theAlert.incConfirm();
            wasConfirmed=true;

            Toast toast = Toast.makeText(getApplicationContext(), "Alert confirmed", Toast.LENGTH_SHORT);
            toast.show();
            confirmButton.setText("Unconfirm");
        }else{  //alert is already confirmed by this user, handle unconfirm
            //remove user from list
            confirmUserList.remove(currentUserEmail);
            usersList.addAll(confirmUserList);

            //decrement alert's confirm
            theAlert.decConfirm();
            wasConfirmed=false;

            Toast toast = Toast.makeText(getApplicationContext(), "Alert unconfirmed", Toast.LENGTH_SHORT);
            toast.show();

            confirmButton.setText("Confirm");
        }

        //update Alert's confirm number
        Map<String, Object> updateContent = new HashMap<String, Object>();
        updateContent.put("confirmed", theAlert.confirmed);
        alertListReference.updateChildren(updateContent);

        //update confirm's list of user
        Log.d(TAG, usersList.toString());
        updatedConfirmedList.put("users", usersList);
        confirmListReference.updateChildren(updatedConfirmedList);

        //update view
        confirmNumberTextView.setText(theAlert.confirmed + "");
        return true;
    }

    public void setView(){
        final TextView alertTitle = (TextView)findViewById(R.id.alertViewTitle);
        final TextView alertCategory = (TextView)findViewById(R.id.alertViewCategory);
        final TextView alertLocation = (TextView)findViewById(R.id.alertViewLocation);
        confirmNumberTextView = (TextView)findViewById(R.id.confirm_number_view);

        alertTitle.setText(theAlert.name);
        alertCategory.setText(theAlert.category);
        confirmNumberTextView.setText(theAlert.confirmed+"");

        //Temporary location
        alertLocation.setText("Location: "+theAlert.latitude+","+theAlert.longitude);
        setLoc(theAlert.latitude,theAlert.longitude);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

}
