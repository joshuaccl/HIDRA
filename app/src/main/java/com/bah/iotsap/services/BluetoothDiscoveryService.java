package com.bah.iotsap.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothDiscoveryService extends Service {

    private static final String TAG = "BTDiscoveryService";

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothDiscoveryService() {
        Log.i(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate(): Entered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy(): Entered");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind(): Entered");
        return null;
    }
}
