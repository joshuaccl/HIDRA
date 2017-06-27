package com.bah.iotsap.services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class ServiceManager extends Service {

    private static final String TAG = "ServiceManager";
    public  static final String START_INTENT_ACTION = "com.bah.iotsap.services.ServiceManager.START";
    public  static final String STOP_INTENT_ACTION  = "com.bah.iotsap.services.ServiceManager.STOP";

    // Booleans indicating which services are running
    private boolean btDiscEnabled  = false;
    private boolean bleDiscEnabled = false;
    // Booleans indicating which permissions are enabled
    private boolean btAdapterEnabled    = false;
    private boolean fineLocationEnabled = false;

    /**
     * Receiver to listen for changes to the state of permissions.
     * This will be used to check what is enabled, and if permissions
     * change at runtime. This will allow us to stop services that will
     * no longer work.
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // RESPOND TO CHANGES IN BT ADAPTER
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.i(TAG, "receiver: BT ACTION_STATE_CHANGED");
            }
        }
    };

    public ServiceManager() {
        Log.i(TAG, "Constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);   // Adapter on or off?
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);   // Enabled, disabled, enabling?
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);// Network connectivity change?
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        if(intent != null && intent.getAction().equals(START_INTENT_ACTION)) {
            /**
             * Ensure that bluetooth and fine location are enabled and usable,
             * Start which ever services are available at this time.
             */
            btAdapterEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
            fineLocationEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            if (btAdapterEnabled && !btDiscEnabled) {
                Log.i(TAG, "onStartCommand(): bt adapter enabled, starting btDiscService");
                btDiscEnabled = true;
                startService(new Intent(this, BluetoothDiscoveryService.class));
            }
        } else if(intent != null && intent.getAction().equals(STOP_INTENT_ACTION)) {
            // TODO: STOP THE SERVICE AND ALL SUBSERVICES
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Constructor");
        return null;
    }
}
