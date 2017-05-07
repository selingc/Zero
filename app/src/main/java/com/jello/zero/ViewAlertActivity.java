package com.jello.zero;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;
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
import java.util.zip.Inflater;

/**
 * Created by hoangphat1908 on 3/19/2017.
 */

public class ViewAlertActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private LatLng location = new LatLng(0,0);
    private String TAG = "ViewAlertActivity";
    private SupportMapFragment mapFragment;
    private Alert theAlert;
    private DatabaseReference confirmListReference;
    private DatabaseReference alertListReference;
    private boolean wasConfirmed = false;
    private Set<String> confirmUserList = new HashSet<String>();
    private String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private Button confirmButton = null;
    private DatabaseReference commentReference;


    //List view stuff
    private ListView commentListView;
    private ChildEventListener commentListener;
    private ChildEventListener confirmedUserListener;
    //private CommentListViewAdapter commentListViewAdapter;
    private List<String> commentsListData;
    private ArrayAdapter<String> commentDefaultArrayAdapter;
    private LayoutInflater inflater;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alert);

        theAlert = (Alert) getIntent().getSerializableExtra("alert");
        confirmButton = (Button)findViewById(R.id.viewAlert_confirm_button);
        setView();

        //reference for theAlert and the confirm list
        confirmListReference = FirebaseDatabase.getInstance().getReference().child("confirm").child(theAlert.key);
        alertListReference = FirebaseDatabase.getInstance().getReference().child("alerts").child(theAlert.key);
        commentReference = FirebaseDatabase.getInstance().getReference().child("comments").child(theAlert.key);

        //get list of users who have confirmed theAlert
        confirmedUserListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                confirmUserList.add(dataSnapshot.getValue().toString());
                setView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                confirmUserList.remove(dataSnapshot.getValue().toString());
                setView();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        confirmListReference.child("users").addChildEventListener(confirmedUserListener);

        Context context = this.getApplicationContext();

        final LinearLayout list = (LinearLayout) findViewById(R.id.comment_linearview);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        commentListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                Log.d(TAG, "onChildAdded comment "+comment.toString());
                /*
                commentsListData.add(comment.toString());
               // commentListViewAdapter.notifyDataSetChanged();
                commentDefaultArrayAdapter.notifyDataSetChanged();*/
                View vi = inflater.inflate(R.layout.comment_row, null);
                TextView content = (TextView) vi.findViewById(R.id.comment_body_field);
                content.setText(comment.getContent());
                content.setTextColor(Color.GRAY);
                TextView author = (TextView) vi.findViewById(R.id.comment_author_field);
                author.setText("@" + comment.getAuthor());
                author.setTextColor(Color.BLACK);
                list.addView(vi);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        commentReference.addChildEventListener(commentListener);
    }

    public void onStop(){
        super.onStop();
        commentReference.removeEventListener(commentListener);
        confirmListReference.child("users").removeEventListener(confirmedUserListener);
      //  commentsListData.clear();
      //  commentDefaultArrayAdapter.notifyDataSetChanged();
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

    public void commentButtonClick(View view){
        Log.d(TAG, "comment button clicked");
        EditText commentField = (EditText) findViewById(R.id.comment_editText);
        Button commentPostButton = (Button) findViewById(R.id.comment_post_button);
        commentField.setVisibility(View.VISIBLE);
        commentPostButton.setVisibility(View.VISIBLE);
    }

    public void postComment(View view){
        EditText commentField = (EditText) findViewById(R.id.comment_editText);
        Button commentPostButton = (Button) findViewById(R.id.comment_post_button);
        commentField.setVisibility(View.GONE);
        commentPostButton.setVisibility(View.GONE);

        String author = currentUserEmail.substring(0, currentUserEmail.indexOf("@"));
        String content = commentField.getText().toString();

        Comment newComment = new Comment(content, author);

        DatabaseReference newCommentRef =  commentReference.push();
        newCommentRef.setValue(newComment);
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

            Toast toast = Toast.makeText(getApplicationContext(), "Alert verified", Toast.LENGTH_SHORT);
            toast.show();
        }else{  //alert is already confirmed by this user, handle unconfirm
            //remove user from list
            confirmUserList.remove(currentUserEmail);
            usersList.addAll(confirmUserList);

            //decrement alert's confirm
            theAlert.decConfirm();
            wasConfirmed=false;

            Toast toast = Toast.makeText(getApplicationContext(), "Alert unverified", Toast.LENGTH_SHORT);
            toast.show();

            confirmButton.setText("Verify" + " (" + theAlert.confirmed + ")");
        }

        //update Alert's confirm number
        Map<String, Object> updateContent = new HashMap<String, Object>();
        updateContent.put("confirmed", theAlert.confirmed);
        alertListReference.updateChildren(updateContent);

        //update confirm's list of user
        Log.d(TAG, usersList.toString());
        updatedConfirmedList.put("users", usersList);
        confirmListReference.updateChildren(updatedConfirmedList);
        return true;
    }

    public void setView(){
        final TextView alertTitle = (TextView)findViewById(R.id.alertViewTitle);
        final TextView alertCategory = (TextView)findViewById(R.id.alertViewCategory);
        final TextView alertLocation = (TextView)findViewById(R.id.alertViewLocation);
        if(confirmUserList.contains(currentUserEmail)){
            confirmButton.setText("Unverify" + " (" + theAlert.confirmed + ")");
            wasConfirmed = true;
        }else{
            confirmButton.setText("Verify" + " (" + theAlert.confirmed + ")");
            wasConfirmed = false;
        }

        alertTitle.setText(theAlert.name);
        alertCategory.setText(theAlert.category);

        //Temporary location
        alertLocation.setText("Location:\n"+theAlert.getLocation());
        setLoc(theAlert.latitude,theAlert.longitude);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

}
