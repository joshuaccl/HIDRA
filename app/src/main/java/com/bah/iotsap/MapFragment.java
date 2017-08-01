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
import com.mapbox.mapboxsdk.style.layers.Filter;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
            ////////////////////////////////////////////////////////////////////
            // DUMMY DATA in HONOLULU
            String[] scanTypes  = new String[]{"rfid", "nfc", "bt", "ble"};
            List<Feature> feats = new ArrayList<>();
            double latw = 21.31100,   late = 21.308200;
            double lngn = -157.864354, lngs = -157.859097;
            Random rand = new Random();
            for(int i = 0; i < 600; ++i) {
                Feature tempFeat = Feature.fromGeometry(
                        Point.fromCoordinates(
                                new double[]{
                                        rand.nextDouble() * Math.abs(lngn - lngs) + Math.min(lngn, lngs),
                                        rand.nextDouble() * Math.abs(latw - late) + Math.min(latw, late)})
                );
                tempFeat.addStringProperty("scan", scanTypes[rand.nextInt(scanTypes.length)]);
                feats.add(tempFeat);
            }
            Log.i(TAG, "TESTINGASFSAEFSDF");
            Log.i(TAG, "feats size = " + feats.size());
            FeatureCollection dummyCol = FeatureCollection.fromFeatures(feats);
            GeoJsonSource dummySource = new GeoJsonSource("dummy-source",
                    dummyCol,
                    new GeoJsonOptions()
                        .withCluster(true)
                        .withClusterMaxZoom(17)
                        .withClusterRadius(12)
            );
            mapboxMap.addSource(dummySource);
            CircleLayer dummyLayer = new CircleLayer("dummy", "dummy-source");
            dummyLayer.withProperties(
                    PropertyFactory.circleRadius(
                            Function.zoom(
                                    Stops.exponential(
                                            Stop.stop(12, PropertyFactory.circleRadius(2f)),
                                            Stop.stop(22, PropertyFactory.circleRadius(180f))
                                    ).withBase(1.75f)
                            )
                    ),
                    PropertyFactory.circleColor(
                            Function.property("scan", Stops.categorical(
                                    Stop.stop(scanTypes[0], PropertyFactory.circleColor(Color.parseColor("#fbb03b"))),
                                    Stop.stop(scanTypes[1], PropertyFactory.circleColor(Color.parseColor("#223b53"))),
                                    Stop.stop(scanTypes[2], PropertyFactory.circleColor(Color.parseColor("#e55e5e"))),
                                    Stop.stop(scanTypes[3], PropertyFactory.circleColor(Color.parseColor("#3bb2d0")))
                                    )
                            )
                    )
            );
            mapboxMap.addLayerBelow(dummyLayer, "road");


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
            Layer layer = mapboxMap.getLayer("sf2010");
            if(layer == null) {
                Log.i(TAG, "LAYER IS NULL");
            } else {
                Log.i(TAG, "LAYER IS NOT NULL");
            }


            ////////////////////////////////////////////////////////////////
            // Mapbox LineLayer with GeoJson Example
            List<Position> routeCoordinates = new ArrayList<>();
            routeCoordinates.add(Position.fromCoordinates(-157.858333, 21.306944));
            routeCoordinates.add(Position.fromCoordinates(-157.858333, 22.306944));

            LineString lineString = LineString.fromCoordinates(routeCoordinates);
            FeatureCollection collection =
                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(lineString)});
            Log.i(TAG, collection.toJson());
            Source geoJsonSource = new GeoJsonSource("line-source", collection);
            mapboxMap.addSource(geoJsonSource);

            LineLayer lineLayer = new LineLayer("line-layer", "line-source");
            lineLayer.setProperties(
                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineColor(Color.RED)
            );
            mapboxMap.addLayer(lineLayer);


            // GEOJSON CIRCLE LAYER FROM URL TEST
            try {
                GeoJsonSource geoSource = new GeoJsonSource("earthquakes",
                        new URL("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                        new GeoJsonOptions()
                            .withCluster(true)
                            .withClusterMaxZoom(15)
                            .withClusterRadius(20)
                );
                mapboxMap.addSource(geoSource);
            } catch (MalformedURLException e) {
                Log.i(TAG, "MalformedURLException", e);
            }
            CircleLayer unclustered = new CircleLayer("unclustered-points", "earthquakes");
            unclustered.setProperties(
                    PropertyFactory.circleColor(Color.parseColor("#FBB03B")),
                    PropertyFactory.circleRadius(20f),
                    PropertyFactory.circleBlur(1f)
            );
            unclustered.setFilter(Filter.neq("cluster", true));
            mapboxMap.addLayerBelow(unclustered, "building");

            // GEOJSON CIRCLE LAYER FROM COLLECTION TEST
            // USE THIS EXAMPLE TO POPULATE MAP!!!! THIS WORKS!!
            List<Feature> featList = new ArrayList<>();
            Feature myFeat;

            myFeat = Feature.fromGeometry(Point.fromCoordinates(new double[]{-157.858333, 21.306944}));
            myFeat.addStringProperty("title", "Honolulu");
            featList.add(myFeat);
            myFeat = Feature.fromGeometry(Point.fromCoordinates(new double[]{-157.817, 21.297}));
            myFeat.addStringProperty("title", "Manoa");
            featList.add(myFeat);
            FeatureCollection coll = FeatureCollection.fromFeatures(featList);
            Log.i(TAG, coll.toJson());

            GeoJsonSource mySource = new GeoJsonSource("testing", coll,
                    new GeoJsonOptions()
                        .withCluster(true)
                        .withClusterMaxZoom(15)
                        .withClusterRadius(20));
            mapboxMap.addSource(mySource);
            CircleLayer testLayer = new CircleLayer("test-layer", "testing");
            testLayer.setProperties(
                    PropertyFactory.circleColor(Function.property("title", Stops.categorical(
                            Stop.stop("Honolulu", PropertyFactory.circleColor(Color.BLUE)),
                            Stop.stop("Manoa", PropertyFactory.circleColor(Color.RED))
                    ))),
                    PropertyFactory.circleRadius(
                            Function.zoom(Stops.exponential(
                                    Stop.stop(12, PropertyFactory.circleRadius(5f)),
                                    Stop.stop(22, PropertyFactory.circleRadius(180f))
                            ).withBase(1.75f)))
            );
            mapboxMap.addLayerBelow(testLayer, "road");
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
                ((MainActivity) getActivity()).viewPager.setCurrentItem(MainActivity.SCAN_INDEX);
            } else if(getResources().getString(R.string.dropdown_item_rfid).equals(item.getTitle())) {
                Log.i(TAG, "onMenuItemClick(MenuItem): RFID ACTION");
                ((MainActivity) getActivity()).viewPager.setCurrentItem(MainActivity.SCAN_INDEX);
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
