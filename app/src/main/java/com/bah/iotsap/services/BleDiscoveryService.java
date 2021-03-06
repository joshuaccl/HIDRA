package com.bah.iotsap.services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.bah.iotsap.App;

import com.bah.iotsap.db.SQLDB;
import com.bah.iotsap.util.DBUtil;
import com.bah.iotsap.util.FileRW;
import com.bah.iotsap.util.LocationDiscovery;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class periodically scans for BLE devices. When it finds a device, it sends a JSON string
 * containing information about the device in an intent broadcast that anyone can receive by
 * registering a receiver for the "RECEIVE_JSON" action.
 * This service will only operate if it has determined nothing should impede it.
 * TODO: Get scan schedule information from sharedPreferences
 * TODO: Communicate with Bluetooth service when both are running to coordinate scans
 * TODO: Acquire locational data to send with each device intent
 */
public class BleDiscoveryService extends Service {

    private static final String TAG = "BleDiscoveryService";
    // Intent Action Strings
    public static final String RECEIVE_JSON = "com.bah.iotsap.services.BleDiscoveryService.RECEIVE_JSON";
    public static final String START = "com.bah.iotsap.services.BleDiscoveryService.START";
    public static final String STOP  = "com.bah.iotsap.services.BleDiscoveryService.STOP";
    // Preference strings
    public static final String PREF_BLE_SERVICE  = "pref_ble_service";
    public static final String PREF_BLE_SCANTIME = "pref_ble_scantime";
    public static final String PREF_BLE_DELAY    = "pref_ble_delay";

    private BluetoothAdapter bleAdapter;    // Default bluetooth adapter
    private BluetoothLeScanner leScanner;   // Default leScanner
    private Handler handler;                // Used for scheduling scans
    private boolean scanning;               // Tell if a scan is currently running
    private long scantime;                  // Time to actively scan for devices
    private long delay;                     // Time to wait after a scan before restarting
    private LocationDiscovery mLocationDiscovery; //Location class for getting the location
    private Context mContext;

    private ArrayList rows;

    private int id = App.ID;

    private final Runnable stopper = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "stopper: stopping scan");
            scanning = false;
            leScanner.stopScan(leScanCallback);
        }
    };
    private final Runnable starter = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "starter: starting scanLeDevice");
            scanLeDevice(scanning);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        mContext = this;

        rows = new ArrayList<>();

        // Initial instantiation with default values
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        handler  = new Handler();
        scanning = false;
        scantime = 7000;
        delay    = 7000;

        // Ensure operation can run successfully
        if(!hasPermissions()) {
            Log.i(TAG, "onCreate(): Do not have all permissions required to run. Stopping self");
            stopSelf();
        }

        mLocationDiscovery = new LocationDiscovery();
        mLocationDiscovery.configureLocationClass(this);
        mLocationDiscovery.startLocationUpdates();

        leScanner = bleAdapter.getBluetoothLeScanner();

        FileRW.init(mContext, "ble");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        if(START.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): action = " + START + ", about to begin scanLeDevice");
            scanLeDevice(scanning);
        } else if(STOP.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): action = " + STOP);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        leScanner.stopScan(leScanCallback);
        handler.removeCallbacks(stopper);
        handler.removeCallbacks(starter);
        Log.i(TAG, "onDestroy(): removed callbacks");
    }

    private void scanLeDevice(final boolean isScanning) {
        if(!isScanning) {
            Log.i(TAG, "scanLeDevice(): Currently not scanning, adding postDelayed");
            // Set runnable to stop the scan after some time
            handler.postDelayed(stopper, scantime);
            handler.postDelayed(starter, scantime + delay);

            Log.i(TAG, "scanLeDevice(): starting scan");
            scanning = true;
            leScanner.startScan(leScanCallback);
        } else {
            Log.i(TAG, "scanLeDevice(): stopping scan");
            scanning = false;
            leScanner.stopScan(leScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i(TAG, "ScanCallback()");

            BluetoothDevice device = result.getDevice();
            String deviceName = device.getName();
            if(deviceName == null) deviceName = "Unknown Device";
            String deviceMac  = device.getAddress();
            int    rssi       = result.getRssi();
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String time = date.substring(8);
            date = date.substring(0,8);
            Location location = mLocationDiscovery.getLocation();

            try {
                JSONObject item = new JSONObject();
                item.put("date", date);
                item.put("time", time);
                item.put("mac", deviceMac);
                item.put("name", deviceName);
                if(location != null) {
                    item.put("latitude", location.getLatitude());
                    item.put("longitude", location.getLongitude());
                    item.put("altitude", location.getAltitude());
                }
                item.put("id",id);
                item.put("rssi",rssi);
                item.put("type", "BLE");

                Log.d(TAG, "ScanCallback(): json = " + item.toString());

                FileRW.append(mContext, "ble", item.toString());

                Intent deviceInfo = new Intent(RECEIVE_JSON).putExtra("json", item.toString());
                LocalBroadcastManager.getInstance(BleDiscoveryService.this).sendBroadcast(deviceInfo);
            } catch(JSONException e) {
                Log.i(TAG, "ScanCallback(): caught JSON exception");
            }

            //Insert new data into database
            ContentValues contentValues = DBUtil.insert(date, time, deviceMac, deviceName, location, id, rssi, "ble");
            long newRowId = App.db.insert(SQLDB.DataTypes.TABLE_NAME, null, contentValues);
            rows.add(newRowId);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "onBatchScanResults");

            for(ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                String deviceName = device.getName();
                String deviceMac  = device.getAddress();
                int    rssi       = result.getRssi();
                String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                Location location = mLocationDiscovery.getLocation();

                try {
                    JSONObject item = new JSONObject();
                    item.put("date", date);
                    item.put("mac", deviceMac);
                    item.put("name", deviceName);
                    item.put("rssi", rssi);
                    item.put("latitude", location.getLatitude());
                    item.put("longitude", location.getLongitude());
                    item.put("altitude", location.getAltitude());
                    Log.i(TAG, "onBatchScanResults(): json = " + item.toString());

                    Intent deviceInfo = new Intent(RECEIVE_JSON).putExtra("json", item.toString());
                    LocalBroadcastManager.getInstance(BleDiscoveryService.this).sendBroadcast(deviceInfo);
                } catch(JSONException e) {
                    Log.i(TAG, "onBatchScanResults(): caught a JSON Exception");
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "onScanFailed()");
        }
    };

    private boolean hasPermissions() {
        Log.i(TAG, "hasPermissions()");
        int fineLocationCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(fineLocationCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "hasPermissions(): no FINE LOCATION");
            return false;
        } else if(bleAdapter == null) {
            Log.i(TAG, "hasPermissions(): no BT ADAPTER");
            return false;
        } else return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}