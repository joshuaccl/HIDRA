package com.bah.iotsap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.mapbox.mapboxsdk.maps.MapView;


/**
 * MapFragment is used as the primary fragment of the activity. This fragment is used to visualize
 * all data of the app.
 * We will either use a MapFragment or create a MapActivity for the final product.
 */
public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private MapView mapView;
    private ImageButton moreBtn;
    private View.OnClickListener moreBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick()");
            PopupMenu menu = new PopupMenu(getActivity().getApplicationContext(), moreBtn);
            menu.getMenuInflater().inflate(R.menu.more_dropdown_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(menuItemClickListener);
            menu.show();
        }
    };
    private PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.i(TAG, "onMenuItemClick(MenuItem): title = " + item.getTitle());

            if(getResources().getString(R.string.dropdown_item_nfc).equals(item.getTitle())) {
                Log.i(TAG, "onMenuItemClick(MenuItem): NFC ACTION");
                ((MainActivity) getActivity()).viewPager.setCurrentItem(MainActivity.NFC_INDEX);
            } else if(getResources().getString(R.string.dropdown_item_rfid).equals(item.getTitle())) {
                Log.i(TAG, "onMenuItemClick(MenuItem): RFID ACTION");
            } else if(getResources().getString(R.string.dropdown_item_pref).equals(item.getTitle())) {
                Log.i(TAG, "onMenuItemClick(MenuItem): PREF ACTION");
                ((MainActivity) getActivity()).viewPager.setCurrentItem(MainActivity.PREF_INDEX);
            }
            return true;
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

        // UI setup
        moreBtn = (ImageButton) fragmentLayout.findViewById(R.id.map_top_linear_more_btn);
        moreBtn.setOnClickListener(moreBtnListener);

        // MapView setup
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
