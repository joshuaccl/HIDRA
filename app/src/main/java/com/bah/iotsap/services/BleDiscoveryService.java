package com.bah.iotsap.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BleDiscoveryService extends Service {

    public  static final String RECEIVE_JSON = "com.bah.iotsap.services.BleDiscoveryService.RECEIVE_JSON";
    public  static final String START = "com.bah.iotsap.services.BleDiscoveryService.START";
    public  static final String STOP  = "com.bah.iotsap.services.BleDiscoveryService.STOP";
    private static final String TAG   = "BleDiscoveryService";

    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner leScanner;
    private Handler handler;
    private boolean scanning;
    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        bleAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bleAdapter == null) stopSelf();
        else leScanner = bleAdapter.getBluetoothLeScanner();
        handler = new Handler();
        scanning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        if(START.equals(intent.getAction())) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(true);
                }
            }, SCAN_PERIOD);

        } else if(STOP.equals(intent.getAction())) {

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    private void scanLeDevice(final boolean enable) {
        if(enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    leScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            leScanner.startScan(leScanCallback);
        } else {
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
            String deviceMac  = device.getAddress();
            int    rssi       = result.getRssi();
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            try {
                JSONObject item = new JSONObject();
                item.put("date", date);
                item.put("mac", deviceMac);
                item.put("name", deviceName);
                item.put("rssi", rssi);
                Log.i(TAG, "ScanCallback(): json = " + item.toString());

                Intent deviceInfo = new Intent(RECEIVE_JSON).putExtra("json", item.toString());
                LocalBroadcastManager.getInstance(BleDiscoveryService.this).sendBroadcast(deviceInfo);
            } catch(JSONException e) {
                Log.i(TAG, "ScanCallback(): caught JSON exception");
            }
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

                try {
                    JSONObject item = new JSONObject();
                    item.put("date", date);
                    item.put("mac", deviceMac);
                    item.put("name", deviceName);
                    item.put("rssi", rssi);
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
