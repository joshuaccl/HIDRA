package com.bah.iotsap;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.bah.iotsap.db.SQLDB;
import com.bah.iotsap.util.DBUtil;
import com.bah.iotsap.util.GeoJsonClusteringActivity;
import com.bah.iotsap.util.LocationDiscovery;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;

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
public class MapFragment extends Fragment implements PermissionsListener, OnMapReadyCallback {

    private static final String TAG = "MapFragment";

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private MapboxMap mapboxMap;

    private LocationDiscovery locationDisc;
    private ImageButton moreBtn;
    private Button filtBtn;
    private Button updtBtn;

    /////////////////// ALL LISTENERS AND CALLBACKS ///////////////////
    MapboxMap.OnMapClickListener mapClickListener = new MapboxMap.OnMapClickListener() {
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
    private View.OnClickListener filtBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "filt onClick()");
            startActivity(new Intent(getActivity(), GeoJsonClusteringActivity.class));
        }
    };
    private View.OnClickListener updtBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "updt onClick()");
            updateMapFromDB();
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
        filtBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_filt_btn);
        updtBtn = (Button)      fragmentLayout.findViewById(R.id.map_top_linear_updt_btn);
        moreBtn.setOnClickListener(moreBtnListener);
        filtBtn.setOnClickListener(filtBtnListener);
        updtBtn.setOnClickListener(updtBtnListener);

        // MapView setup
        mapView = (MapView) fragmentLayout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {

        Log.i(TAG, "onMapReady(MapboxMap)");
        this.mapboxMap = mapboxMap;

        //__________________________________________________________________________________
        //Hard coding a marker

        LatLng impacthub = new LatLng(21.294907, -157.852051);
        mapboxMap.addMarker(new MarkerOptions().position(impacthub).title("Impact Hub Honolulu")
        .snippet("Colleen wants to go sleep"));

        //Add a marker in Honolulu Hawaii and move the camera

        //Parking spots near impact hub
        LatLng oahu_church = new LatLng(21.296321, -157.851863);
        mapboxMap.addMarker(new MarkerOptions().position(oahu_church).title("Oahu Church of Christ"));
        LatLng aloha_dog = new LatLng(21.296558, -157.852327);
        mapboxMap.addMarker(new MarkerOptions().position(aloha_dog).title("Aloha Dog"));
        LatLng parking_lot = new LatLng(21.294561, -157.852420);
        mapboxMap.addMarker(new MarkerOptions().position(parking_lot).title("Parking Lot"));
        LatLng ward_theatre = new LatLng(21.294486, -157.853361);
        mapboxMap.addMarker(new MarkerOptions().position(ward_theatre).title("Ward Theatre"));
        LatLng modern_detail = new LatLng(21.295691, -157.851237);
        mapboxMap.addMarker(new MarkerOptions().position(modern_detail).title("Modern Detail"));
        LatLng phuket_thai = new LatLng(21.294540, -157.851424);
        mapboxMap.addMarker(new MarkerOptions().position(phuket_thai).title("Phuket Thai"));
        LatLng prestige_valet = new LatLng(21.294364, -157.850797);
        mapboxMap.addMarker(new MarkerOptions().position(prestige_valet).title("Prestige Valet"));
        LatLng uhaul = new LatLng(21.295441, -157.851130);
        mapboxMap.addMarker(new MarkerOptions().position(uhaul).title("U-Haul"));
        LatLng tint_shop = new LatLng(21.295608, -157.852322);
        mapboxMap.addMarker(new MarkerOptions().position(tint_shop).title("Tint Shop Hawaii"));
        LatLng bliss_day = new LatLng(21.295022, -157.850847);
        mapboxMap.addMarker(new MarkerOptions().position(bliss_day).title("Bliss Day Spa"));

        // Ensure we have location permissions
        permissionsManager = new PermissionsManager(MapFragment.this);
        if(!PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            Log.i(TAG, "onMapReady(): Permissions not granted");
            permissionsManager.requestLocationPermissions(getActivity());
        } else {
            enableLocationTracking();
        }
        // Setup action to happen on map click
        mapboxMap.setOnMapClickListener(mapClickListener);

        ////////////////////////////////////////////////////////////////////
        String[] scanTypes  = new String[]{"rfid", "nfc", "bt", "ble", "beacon"};
        List<Feature> feats = new ArrayList<>();
        double latw = 21.31100,    late = 21.308200;
        double lngn = -157.864354, lngs = -157.859097;
        Random rand = new Random();


        /* TEST DATA IN HONOLULU

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
                    .withClusterMaxZoom(16)
                    .withClusterRadius(8)
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
        */

        // MAPBOX SANFRANCISCO POINT DEMO //
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

    private void updateMapFromDB() {

        String[] scanTypes  = new String[]{"rfid", "nfc", "bt", "ble", "beacon"};

        Cursor cursor = DBUtil.read(App.db, SQLDB.DataTypes.TABLE_NAME);
        List<Feature> myDataFeats = new ArrayList<>();
        //take out data and put into this list ^
        while(cursor.moveToNext()) {
            String tempLong = cursor.getString(cursor.getColumnIndex(SQLDB.DataTypes.COLUMN_LON));
            String tempLat  = cursor.getString(cursor.getColumnIndex(SQLDB.DataTypes.COLUMN_LAT));
            if(tempLong == null || tempLat == null) {
                Log.i(TAG, "got null item");
                continue;
            }
            Log.i(TAG, "lat = " + tempLat + ", lng = " + tempLong);
            Feature tempFeat = Feature.fromGeometry(Point.fromCoordinates(
                    new double[]{
                            Double.parseDouble(tempLong),
                            Double.parseDouble(tempLat)
                    }
            ));
            tempFeat.addStringProperty("scan", cursor.getString(cursor.getColumnIndex(SQLDB.DataTypes.COLUMN_TYPE)));
            myDataFeats.add(tempFeat);
        }

        FeatureCollection myDataCollection = FeatureCollection.fromFeatures(myDataFeats);
        //geo json file with all the features from list
        Log.i(TAG, "myDataCollection=" + myDataCollection.toJson());

        //EDIT ME
        //COLLECTING BLU DEVICES > PLOT PRETTY
        GeoJsonSource myDataSource = (GeoJsonSource) mapboxMap.getSource("mydata-source");
        if(myDataSource == null) {
            myDataSource = new GeoJsonSource(
                    "mydata-source",
                    myDataCollection,
                    new GeoJsonOptions()
                            //adjust parameters to make pretty
                            //stops affect how big circles are

                            .withCluster(true)
                            .withClusterRadius(3)
                            .withClusterMaxZoom(14)
            );
            mapboxMap.addSource(myDataSource);
            CircleLayer myDataLayer = new CircleLayer("mydata", "mydata-source");
            myDataLayer.withProperties(
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
                                    Stop.stop(scanTypes[3], PropertyFactory.circleColor(Color.parseColor("#3bb2d0"))),
                                    Stop.stop(scanTypes[4], PropertyFactory.circleColor(Color.GREEN))
                                    )
                            )
                    )
            );

            //STOP HERE
            mapboxMap.addLayerBelow(myDataLayer, "road");
        } else {
            myDataSource.setGeoJson(myDataCollection);
        }
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
