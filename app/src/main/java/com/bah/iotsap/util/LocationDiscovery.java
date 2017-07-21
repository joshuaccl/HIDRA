package com.bah.iotsap.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class LocationDiscovery {
    private static FusedLocationProviderClient mFusedLocationClient;
    private static Location mLocation;
    private static LocationCallback mLocationCallback;
    private static LocationRequest mLocationRequest;
    private static LocationRequest currentLocationRequest;
//    private Activity mActivity;
    private Context mContext;
    private static int count;
    private static int clickCount;

    //Constructor
    public LocationDiscovery() {}

    //Call this method to first initialize location discovery
    public void configureLocationClass(Context context) {

        mContext = context;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        createLocationRequest();

        count = 0;
        clickCount = 0;


//        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
//        }


//        mFusedLocationClient.getLastLocation().addOnSuccessListener(
//                mActivity, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if(location != null) {
//                            mLocation = location;
//                        }
//                        else {
//                            Toast.makeText(mActivity, "Last Known Location is Unknown", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            );

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()) {
                    if(location != null) {
                        mLocation = location;
                        count++;
                    }
                }
            }
        };

        startLocationUpdates();
    }

    //call this method to receive the location
    public Location getLocation() {
        clickCount++;
        Log.d("Click", Integer.toString(clickCount));
        currentLocationRequest = new LocationRequest();
        currentLocationRequest.setInterval(10000);
        currentLocationRequest.setFastestInterval(5000);
        currentLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            mFusedLocationClient.requestLocationUpdates(currentLocationRequest,
                    mLocationCallback,
                    null);
        } catch (SecurityException e) {
        }

        return mLocation;
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        } catch (SecurityException e) {

        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}