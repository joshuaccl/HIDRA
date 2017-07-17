package com.bah.iotsap;

import android.app.Application;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;

/**
 * App class is the base class of the entire app, used for maintaining global application state.
 * This class is instantiated before any other class when the process for the app is created.
 */
public class App extends Application {

    private static final String TAG = "Application";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        Mapbox.getInstance(getApplicationContext(), getResources().getString(R.string.mapbox_token));
        Log.i(TAG, "onCreate(): Got mapbox instance");
    }
}
