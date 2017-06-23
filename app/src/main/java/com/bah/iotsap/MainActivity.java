package com.bah.iotsap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * This activity is basically a container for various fragments. It consists
 * only of a ViewPager, which is populated upon creation with various fragments
 * which can be swiped between.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private static final int NUM_FRAGMENTS = 5;
    private static final int START_INDEX   = 1;


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

        public PagerAdapter(android.app.FragmentManager fm) {
            super(fm);
            Log.i(TAG, "PagerAdapter: constructor");
        }
        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "PagerAdapter: getItem, position = " + position);
            switch(position) {
                case 0:  return new SettingsFragment();
                case 1:  return new MapFragment();
                case 2:  return new TaskFragment(); // Bluetooth fragment
                case 3:  return new BleFragment(); // BLE fragment
                case 4:  return new TaskFragment(); // NFC fragment
                default: return null;
            }
        }

        /**
         * This function dictates how many screens you can swipe through.
         * @return number of fragment pages to have as an int
         */
        @Override
        public int getCount() {
            return NUM_FRAGMENTS;
        }
    }
}
