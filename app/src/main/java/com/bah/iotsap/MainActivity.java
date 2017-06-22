package com.bah.iotsap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;

/**
 * This activity is basically a container for various fragments. It consists
 * only of a ViewPager, which is populated upon creation with various fragments
 * which can be swiped between.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private static final int NUM_FRAGMENTS = 5;
    private static final int START_INDEX   = 1;
    private static final int PREF_INDEX    = 0;
    private static final int MAP_INDEX     = 1;
    private static final int BT_INDEX      = 2;
    private static final int BLE_INDEX     = 3;
    private static final int NFC_INDEX     = 4;

    PagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: setContentView");
        setContentView(R.layout.activity_main);

        /**
         * Attach the pager adapter to the viewPager layout of the activity.
         * This allows us to swipe between fragments on the same activity.
         */
        Log.i(TAG, "onCreate: Setting up pagerAdapter / viewPager");
        pagerAdapter = new PagerAdapter(getFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.fragment_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(START_INDEX);
        Log.i(TAG, "onCreate: Finished pagerAdapter / viewPager setup");
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>(NUM_FRAGMENTS);

        public PagerAdapter(android.app.FragmentManager fm) {
            super(fm);
            Log.i(TAG, "PagerAdapter: constructor");
        }

        /**
         * Instantiates fragments for each position and stores them in a SparseArray
         * for later retrieval.
         * @param position
         * @return fragment belonging to position
         */
        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "PagerAdapter: getItem, position = " + position);
            switch(position) {
                case PREF_INDEX: registeredFragments.put(position, new SettingsFragment()); break;
                case MAP_INDEX : registeredFragments.put(position, new MapFragment()); break;
                case BT_INDEX  :
                case BLE_INDEX :
                case NFC_INDEX : registeredFragments.put(position, new TaskFragment()); break;
                default: break;
            }
            return registeredFragments.get(position);
        }

        /**
         * This function dictates how many screens you can swipe through.
         * @return number of fragment pages to have as an int
         */
        @Override
        public int getCount() {
            return NUM_FRAGMENTS;
        }

        /**
         * Use to retrieve a fragment at a particular position.
         * Usage: MapFragment frag = (MapFragment) pagerAdapter.getFragmentAt(MAP_INDEX);
         * @param position
         * @return Fragment
         */
        @Nullable
        public Fragment getFragmentAt(int position) {
            Log.i(TAG, "PagerAdapter: getFragmentAt(" + position + ")");
            return registeredFragments.get(position);
        }
    }
}
