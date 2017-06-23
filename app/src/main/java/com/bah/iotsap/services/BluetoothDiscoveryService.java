package com.bah.iotsap.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BluetoothDiscoveryService extends Service {

    private static final String TAG = "BTDiscoveryService";

    private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive()");
            String action = intent.getAction();
            // Discovery found a device, get its information here
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMAC  = device.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                Log.i(TAG, "receiver: " + "name: " + deviceName + "MAC: " + deviceMAC + "RSSI: " + rssi);
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "onReceive(): Restarting BT discovery");
                btAdapter.startDiscovery();
            }
        }
    };

    public BluetoothDiscoveryService() {
        Log.i(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate(): Entered");
        // Bluetooth discovery setup
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        // Check only continue if device supports Bluetooth
        if(btAdapter == null) {
            Log.i(TAG, "onStartCommand(): Device does not support BT, calling stopSelf()");
            stopSelf();
        } else if(!btAdapter.isEnabled()) {
            Log.i(TAG, "onStartCommand(): enabling bluetooth adapter");
            btAdapter.enable();
        }

        Log.i(TAG, "onStartCommand(): Discovering devices");
        btAdapter.startDiscovery();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy(): Entered");
        super.onDestroy();
        Log.i(TAG, "onDestroy(): Unregistering receiver");
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind(): Entered");
        return null;
    }
}
