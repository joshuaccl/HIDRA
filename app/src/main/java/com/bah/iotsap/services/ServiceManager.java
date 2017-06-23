package com.bah.iotsap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceManager extends Service {

    private static final String TAG = "ServiceManager";

    public ServiceManager() {
        Log.i(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        // TODO: Get and check permissions before launching services
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        Log.i(TAG, "onStartCommand(): Starting BluetoothDiscoveryService");
        startService(new Intent(this, BluetoothDiscoveryService.class));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Constructor");
        return null;
    }
}
