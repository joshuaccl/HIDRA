package com.bah.iotsap;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bah.iotsap.util.LocationDiscovery;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.functions.Function;
import com.mapbox.mapboxsdk.style.functions.stops.Stop;
import com.mapbox.mapboxsdk.style.functions.stops.Stops;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;

import java.util.List;


/**
 * MapFragment is used as the primary fragment of the activity. This fragment is used to visualize
 * all data of the app.
 * We will either use a MapFragment or create a MapActivity for the final product.
 */
public class MapFragment extends Fragment implements PermissionsListener {

    private static final String TAG = "MapFragment";

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap mapboxMap;

    private LocationDiscovery locationDisc;
    private ImageButton moreBtn;
    private Button selfBtn;
    private Button stylBtn;
    private Button filtBtn;
    private Button updtBtn;

    /////////////////// ALL LISTENERS AND CALLBACKS ///////////////////
    private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final MapboxMap mapboxMap) {
            Log.i(TAG, "onMapReady(MapboxMap)");
            MapFragment.this.mapboxMap = mapboxMap;

            // Ensure we have location permissions
            permissionsManager = new PermissionsManager(MapFragment.this);
            if(!permissionsManager.areLocationPermissionsGranted(getActivity())) {
                permissionsManager.requestLocationPermissions(getActivity());
            } else {
                enableLocationTracking();
            }

            // Setup action to happen on map click
            mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng point) {
                    final Location location = locationDisc.getLocation();
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(17)
                            .bearing(0)
                            .tilt(30)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5000);
                }
            });

            // Add data points to map from remote source
            VectorSource vectorSource = new VectorSource(
                    "ethnicity-source",
                    "http://api.mapbox.com/v4/examples.8fgz4egr.json?access_token=" + Mapbox.getAccessToken()
            );
            mapboxMap.addSource(vectorSource);

            CircleLayer circleLayer = new CircleLayer("population", "ethnicity-source");
            circleLayer.setSourceLayer("sf2010");
            circleLayer.withProperties(
                    PropertyFactory.circleRadius(
                            Function.zoom(
                                    Stops.exponential(
                                            Stop.stop(12, PropertyFactory.circleRadius(2f)),
                                            Stop.stop(22, PropertyFactory.circleRadius(180f))
                                    ).withBase(1.75f)
                            )
                    ),
                    PropertyFactory.circleColor(
                            Function.property("ethnicity", Stops.categorical(
                                    Stop.stop("white", PropertyFactory.circleColor(Color.parseColor("#fbb03b"))),
                                    Stop.stop("Black", PropertyFactory.circleColor(Color.parseColor("#223b53"))),
                                    Stop.stop("Hispanic", PropertyFactory.circleColor(Color.parseColor("#e55e5e"))),
                                    Stop.stop("Asian", PropertyFactory.circleColor(Color.parseColor("#3bb2d0"))),
                                    Stop.stop("Other", PropertyFactory.circleColor(Color.parseColor("#cccccc")))
                                    )
                            )
                    )
            );
            mapboxMap.addLayer(circleLayer);
        }
    };
    private View.OnClickListener moreBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "more onClick()");
            PopupMenu menu = new PopupMenu(getActivity().getApplicationContext(), moreBtn);
            menu.getMenuInflater().inflate(R.menu.more_dropdown_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(menuItemClickListener);
            menu.show();
        }
    };
    private View.OnClickListener selfBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "self onClick()");
        }
    };
    private View.OnClickListener stylBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "styl onClick()");
        }
    };
    private View.OnClickListener filtBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "filt onClick()");
        }
    };
    private View.OnClickListener updtBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "updt onClick()");
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
    /////////////////// END LISTENERS AND CALLBACKS ///////////////////


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View fragmentLayout = inflater.inflate(R.layout.fragment_map, container, false);

        locationDisc = new LocationDiscovery();
        locationDisc.configureLocationClass(getActivity());
        locationDisc.startLocationUpdates();

        // UI setup
        moreBtn = (ImageButton) fragmentLayout.findViewById(R.id.map_top_linear_more_btn);
        selfBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_self_btn);
        stylBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_styl_btn);
        filtBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_filt_btn);
        updtBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_updt_btn);
        moreBtn.setOnClickListener(moreBtnListener);
        selfBtn.setOnClickListener(selfBtnListener);
        stylBtn.setOnClickListener(stylBtnListener);
        filtBtn.setOnClickListener(filtBtnListener);
        updtBtn.setOnClickListener(updtBtnListener);

        // MapView setup
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapReadyCallback);
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

    private void enableLocationTracking() {
        mapboxMap.setMyLocationEnabled(true);
        mapboxMap.getTrackingSettings().setDismissAllTrackingOnGesture(true);
        mapboxMap.getTrackingSettings().setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        mapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
    }

    // PermissionsListener Overrides for interface
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted) {
            enableLocationTracking();
        } else {
            Toast.makeText(getActivity(), R.string.mapbox_location_permission_denied, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), R.string.mapbox_location_permission_explanation, Toast.LENGTH_LONG).show();
    }
}
