package com.jello.zero;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by hoangphat1908 on 4/22/2017.
 */

public class ShowAlertFragment extends MainFragment{
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new ShowAlertFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getAlertReference() {
        return "alerts";
    }

    @Override
    public Alert retrieveAlert(DataSnapshot snapshot) {
        return snapshot.getValue(Alert.class);
    }


}
