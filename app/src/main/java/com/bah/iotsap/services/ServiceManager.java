package com.bah.iotsap.services;

import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * ServiceManager is responsible for launching other services initially.
 * ServiceManager will launch all possible services it is allowed to by referencing the sharedPreferences
 * for the application as well as checking if the service is currently running.
 * We assume that each service checks if it has all the resources / adapters it needs to run internally.
 */
public class ServiceManager extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "ServiceManager";
    // intent action strings
    public static final String START = "com.bah.iotsap.services.ServiceManager.START";
    public static final String STOP  = "com.bah.iotsap.services.ServiceManager.STOP";

    private SharedPreferences preferences;

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
            } else if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                Log.i(TAG, "onReceive(): BT WIFI_STATE_CHANGED_ACTION");
            } else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                Log.i(TAG, "onReceive(): BT NETWORK_STATE_CHANGED");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        // ServiceManager uses these actions to shut down any services that will no longer work
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);   // Adapter on or off?
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);   // Enabled, disabled, enabling?
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);// Network connectivity change?

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    /**
     * Launch all possible services that can run on hardware and that are allowed in Preferences.
     * We handle launching and stopping them here, however each service first checks to ensure that
     * it can run without encountering errors.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        if(intent != null && START.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): intent action = START");

            // Start Bluetooth service if applicable
            if(preferences.getBoolean(BluetoothDiscoveryService.PREF_BT_SERVICE, false) &&
                    !isServiceRunning(BluetoothDiscoveryService.class)) {
                Log.i(TAG, "onStartCommand(): Starting Bluetooth Service");
                startService(new Intent(BluetoothDiscoveryService.START, null,
                        getApplicationContext(), BluetoothDiscoveryService.class));
            }
            // Start BLE service if applicable
            if(preferences.getBoolean(BleDiscoveryService.PREF_BLE_SERVICE, false) &&
                    !isServiceRunning(BleDiscoveryService.class)) {
                Log.i(TAG, "onStartCommand(): Starting BLE Service");
                startService(new Intent(BleDiscoveryService.START, null,
                        getApplicationContext(), BleDiscoveryService.class));
            }
            //Start Beacon service if applicable
            if(preferences.getBoolean(BeaconDiscoveryService.PREF_BEACON_SERVICE, false) &&
                    !isServiceRunning(BeaconDiscoveryService.class)) {
                Log.i(TAG, "onStartCommand(): Starting Beacon Service");
                startService(new Intent(BeaconDiscoveryService.START, null,
                        getApplicationContext(), BeaconDiscoveryService.class));
            }

        } else if(intent != null && intent.getAction().equals(STOP)) {
            Log.i(TAG, "onStartCommand(): intent action = STOP");

            if(isServiceRunning(BluetoothDiscoveryService.class)) {
                Log.i(TAG, "onStartCommand(): Stopping Bluetooth Service");
                stopService(new Intent(getApplicationContext(), BluetoothDiscoveryService.class));
            }
            if(isServiceRunning(BleDiscoveryService.class)) {
                Log.i(TAG, "onStartCommand(): Stopping BLE Service");
                stopService(new Intent(getApplicationContext(), BleDiscoveryService.class));
            }
            if(isServiceRunning(BeaconDiscoveryService.class)) {
                stopService(new Intent(getApplicationContext(), BeaconDiscoveryService.class));
            }
            Log.i(TAG, "onStartCommand(): Stopping Self");
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        } catch(Exception e) {
            Log.i(TAG, "onDestroy(): EXCEPTION CAUGHT = " + e.getMessage());
        }
    }

    /**
     * If any preferences change for the application, we need to decide if any services need to
     * be started or stopped.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "onSharedPreferenceChanged(): key = " + key);

        // BLUETOOTH SETTINGS
        if(BluetoothDiscoveryService.PREF_BT_SERVICE.equals(key)) {
            if(sharedPreferences.getBoolean(key, false)) {
                Log.i(TAG, "onStaredPreferenceChanged(): Starting Bluetooth Service");
                startService(new Intent(BluetoothDiscoveryService.START, null,
                        getApplicationContext(), BluetoothDiscoveryService.class));
            } else {
                Log.i(TAG, "onSharedPreferenceChanged(): Stopping Bluetooth Service");
                stopService(new Intent(getApplicationContext(), BluetoothDiscoveryService.class));
            }
        }

        // BLE SETTINGS
        if(BleDiscoveryService.PREF_BLE_SERVICE.equals(key)) {
            if(sharedPreferences.getBoolean(key, false)) {
                Log.i(TAG, "onSharedPreferenceChanged(): Starting BLE Service");
                startService(new Intent(BleDiscoveryService.START, null,
                        getApplicationContext(), BleDiscoveryService.class));
            } else {
                Log.i(TAG, "onSharedPreferenceChanged(): Stopping BLE Service");
                stopService(new Intent(getApplicationContext(), BleDiscoveryService.class));
            }
        }

        //Beacon Settings
        if(BeaconDiscoveryService.PREF_BEACON_SERVICE.equals(key)) {
            if(sharedPreferences.getBoolean(key, false)) {
                startService(new Intent(BeaconDiscoveryService.START, null,
                        getApplicationContext(), BeaconDiscoveryService.class));
            } else {
                stopService(new Intent(getApplicationContext(), BeaconDiscoveryService.class));
            }
        }
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
