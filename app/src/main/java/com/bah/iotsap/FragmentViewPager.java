package com.bah.iotsap;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * FragmentViewPager is a class that is used for viewing fragments.
 * FragmentViewPager is almost identical to ViewPager, however
 * this class prevents the user from swiping between fragments.
 */
public class FragmentViewPager extends ViewPager {

    private static final String TAG = "FragmentViewPager";

    public FragmentViewPager(Context context) {
        super(context);
        Log.i(TAG, "FragmentViewPager(Context)");
    }
    public FragmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "FragmentViewPager(Context, AttributeSet)");
    }

    /**
     * Returning super allows swiping between fragments.
     * Returning false prevents any swiping.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // return super.onInterceptTouchEvent(ev);
        return false;
    }

    /**
     * Returning super allows swiping between fragments.
     * Returning false prevents any swiping.
     * Both these methods must be overridden to completely prevent swiping.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // return super.onTouchEvent(ev);
        Log.i(TAG, "onTouchEvent(MotionEvent)");
        return false;
    }
}
