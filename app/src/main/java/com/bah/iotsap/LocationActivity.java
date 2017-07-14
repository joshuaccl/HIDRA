package com.example.a591263.locationexample;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

// !!!IMPORTANT NOTE!!!
// This Sample Application uses the Google Location API
// You must download the API for the code to work properly

public class LocationActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView latitude;
    private TextView longitude;
    private Button getLocation;
    private Location activityLocation;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationRequest currentLocation;
    private int count;
    private int clickCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        count = 0;
        clickCount = 0;

        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        getLocation = (Button) findViewById(R.id.getlocation);

        latitude.setText("null");
        longitude.setText("null");
        getLocation.setText("Get Location");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(
                this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null) {
                            activityLocation = location;
                            String lat = Double.toString(activityLocation.getLatitude());
                            String lon = Double.toString(activityLocation.getLongitude());
                            latitude.setText(lat);
                            longitude.setText(lon);
                        }
                        else {
                            Toast.makeText(LocationActivity.this, "Last Location is Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()) {
                    if(location != null) {
                        activityLocation = location;
                        String lat = Double.toString(activityLocation.getLatitude());
                        String lon = Double.toString(activityLocation.getLongitude());
                        latitude.setText(lat);
                        longitude.setText(lon);
                        count++;
                        Log.d("Update number: " + lat + " " + lon, Integer.toString(count));
                    }
                }
            }
        };

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                Log.d("Click", Integer.toString(clickCount));
                currentLocation = new LocationRequest();
                currentLocation.setInterval(10000);
                currentLocation.setFastestInterval(5000);
                currentLocation.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                try {
                    mFusedLocationClient.requestLocationUpdates(currentLocation,
                            mLocationCallback,
                            null);
                } catch (SecurityException e) {
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
            startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        } catch (SecurityException e) {

        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}