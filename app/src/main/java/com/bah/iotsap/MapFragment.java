package com.bah.iotsap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.mapboxsdk.maps.MapView;


/**
 * MapFragment is used as the primary fragment of the activity. This fragment is used to visualize
 * all data of the app.
 * We will either use a MapFragment or create a MapActivity for the final product.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private MapView mapView;
    private Button moreBtn;     // Button to slide left linear layout down
    private Button nfcBtn;      // Transition to NFC fragment
    private Button rfidBtn;     // Transition to RFID fragment
    private Button prefBtn;     // Transition to preferences fragment
    private boolean moreBtnClicked = false;

    private View.OnClickListener moreBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "moreBtn clicked");
        }
    };
    private View.OnClickListener nfcBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "nfcBtn clicked");
        }
    };
    private View.OnClickListener rfidBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "rfidBtn clicked");
        }
    };
    private View.OnClickListener prefBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "prefBtn clicked");
            ((MainActivity) getActivity()).viewPager.setCurrentItem(MainActivity.PREF_INDEX);
        }
    };

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

        moreBtn = (Button) fragmentLayout.findViewById(R.id.map_left_more_button);
        nfcBtn  = (Button) fragmentLayout.findViewById(R.id.map_left_nfc_button);
        rfidBtn = (Button) fragmentLayout.findViewById(R.id.map_left_rfid_button);
        prefBtn = (Button) fragmentLayout.findViewById(R.id.map_left_pref_button);
        moreBtn.setOnClickListener(moreBtnListener);
        nfcBtn .setOnClickListener(nfcBtnListener);
        rfidBtn.setOnClickListener(rfidBtnListener);
        prefBtn.setOnClickListener(prefBtnListener);
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
