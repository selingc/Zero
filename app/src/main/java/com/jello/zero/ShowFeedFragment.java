package com.jello.zero;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by hoangphat1908 on 4/22/2017.
 */

public class ShowFeedFragment extends MainFragment {
    @Override
    public MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new ShowFeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getAlertReference() {
        return "feed";
    }
    @Override
    public Alert retrieveAlert(DataSnapshot snapshot) {
        Alert alert = new Alert(
                snapshot.child("title").getValue(String.class),
                snapshot.child("description").getValue(String.class),
                snapshot.child("location").getValue(String.class),
                snapshot.child("latitude").getValue(String.class),
                snapshot.child("longtitude").getValue(String.class),
                "",
                0
        );
        return alert;
    }
}
