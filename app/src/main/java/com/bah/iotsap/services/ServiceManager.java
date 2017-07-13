package com.bah.iotsap.services;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class ServiceManager extends Service {

    private static final String TAG   = "ServiceManager";
    public  static final String START = "com.bah.iotsap.services.ServiceManager.START";
    public  static final String STOP  = "com.bah.iotsap.services.ServiceManager.STOP";

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
            Log.i(TAG, "onReceive()");
            String action = intent.getAction();

            // RESPOND TO CHANGES IN BT ADAPTER
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                Log.i(TAG, "onReceive(): BT ACTION_STATE_CHANGED");
            }
        }
    };

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

        if(intent != null && intent.getAction().equals(START)) {
            Log.i(TAG, "onStartCommand(): intent action = START");
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
        } else if(intent != null && intent.getAction().equals(STOP)) {
            Log.i(TAG, "onStartCommand(): intent action = STOP");

            if(isServiceRunning(BluetoothDiscoveryService.class))
                stopService(new Intent(getApplicationContext(), BluetoothDiscoveryService.class));

            if(isServiceRunning(BleDiscoveryService.class))
                stopService(new Intent(getApplicationContext(), BleDiscoveryService.class));
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Method to determine if a service is running. Returns true if the service is running,
     * and false if it is not.
     * @param serviceClass format = "MyService.class"
     * @return true if running, false otherwise
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(service.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
}
