package com.jello.zero;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        MainFragment alertFragment = new ShowAlertFragment();
        MainFragment feedFragment = new ShowFeedFragment();
        AlertMapFragment alertMapFragment = new AlertMapFragment();
        switch (position) {
            case 0:
                return alertFragment.newInstance(1);
            case 1:
                return feedFragment.newInstance(2);
            default:
                return alertMapFragment.newInstance(3);
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ALERTS";
            case 1:
                return "FEEDS";
            case 2:
                return "AREA";
        }
        return null;
    }
}
