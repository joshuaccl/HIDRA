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
 * The class manages swiping permissions internally.
 */
public class FragmentViewPager extends ViewPager {

    private static final String TAG = "FragmentViewPager";
    private boolean isScrollable = false;

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
        return isScrollable && super.onInterceptTouchEvent(ev);
    }

    /**
     * Returning super allows swiping between fragments.
     * Returning false prevents any swiping.
     * Both these methods must be overridden to completely prevent swiping.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(TAG, "onTouchEvent(MotionEvent)");
        return isScrollable && super.onTouchEvent(ev);
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
        listener.setFragmentViewPager(this);

    }

    /**
     * OnPageChangeListener handles what happens when the page is changed either programmatically
     * or by user input.
     * By using this class, other classes can simply worry about what page they want to show up,
     * while this class handles what goes on behind the scenes when anything is changed externally.
     */
    public static class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        private static final String TAG = "OnPageChangeListener";
        private FragmentViewPager viewPager;

        public void setFragmentViewPager(FragmentViewPager vp) {
            Log.i(TAG, "setFragmentViewPager()");
            viewPager = vp;
        }
        @Override
        public void onPageSelected(int position) {
            Log.i(TAG, "onPageSelected(" + position + ")");
            if(position != MainActivity.MAP_INDEX) viewPager.isScrollable = true;
            else viewPager.isScrollable = false;
            Log.i(TAG, "onPageSelected(" + position + ") isScrollable = " + viewPager.isScrollable);

        }
        @Override
        public void onPageScrollStateChanged(int state) {}
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    }
}
