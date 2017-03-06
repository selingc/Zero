package com.jello.zero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangphat1908 on 3/5/2017.
 */

public class CreateAlertActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private static final String TAG = "CreateAlert";
    private DatabaseReference alertsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alert);
        database = FirebaseDatabase.getInstance();
        alertsRef = database.getReference("alerts");
    }

    public void createAlert(View view){
        String name = ((EditText)findViewById(R.id.name_alert)).getText().toString();
        String category = ((EditText)findViewById(R.id.category_alert)).getText().toString();
        String location = ((EditText)findViewById(R.id.location_alert)).getText().toString();
        addAlert(name, category, location);
        alertCreated();
    }

    public void addAlert(String name, String category, String location){
        Alert newAlert = new Alert(name, category, location);
        DatabaseReference newAlertRef = alertsRef.push();
        newAlertRef.setValue(newAlert);
    }

    public void alertCreated(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void switchToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
