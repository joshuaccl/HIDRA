package com.bah.iotsap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.maps.MapView;


/**
 * MapFragment is used as the primary fragment of the activity. This fragment is used to visualize
 * all data of the app.
 * We will either use a MapFragment or create a MapActivity for the final product.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private MapView mapView;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View fragmentLayout = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        return fragmentLayout;
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart()");
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop()");
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG, "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        mapView.onDestroy();
    }
}
