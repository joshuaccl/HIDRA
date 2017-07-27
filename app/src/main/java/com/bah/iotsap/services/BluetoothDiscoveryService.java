package com.bah.iotsap.services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bah.iotsap.App;
import com.bah.iotsap.util.FileRW;
import com.bah.iotsap.util.LocationDiscovery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BluetoothDiscoveryService periodically scans for any and all near-by bluetooth devices.
 * When it finds a device, it sends an intent with all the data from the device as a JSON-string.
 * The service first checks to make sure that it can operate successfully (has all required permissions)
 * before entering its standard scanning loop.
 * TODO: Optimize scheduling of Bluetooth scans
 * TODO: Communicate with BLE service to coordinate scans
 * TODO: Acquire locational data to include with each scanned device
 */
public class BluetoothDiscoveryService extends Service {

    private static final String TAG = "BTDiscoveryService";
    // Intent action strings
    public static final String RECEIVE_JSON = "com.bah.iotsap.services.BluetoothDiscoveryService.RECEIVE_JSON";
    public static final String START = "com.bah.iotsap.services.BluetoothDiscoveryService.START";
    public static final String STOP  = "com.bah.iotsap.services.BluetoothDiscoveryService.STOP";
    // SharedPreferences Strings
    public static final String PREF_BT_SERVICE  = "pref_bt_service";
    public static final String PREF_BT_SCANTIME = "pref_bt_scantime";
    public static final String PREF_BT_DELAY    = "pref_bt_delay";

    private LocationDiscovery mLocationDiscovery;
    private Context mContext;
    private int id = App.ID;

    private final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive()");
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get information from discovered devices
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMac  = device.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String time = date.substring(8);
                date = date.substring(0,7);
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
                    item.put("type", "BT");
                    Log.i(TAG, item.toString());

                    FileRW.append(mContext, "bt", item.toString());

                    Intent deviceInfo = new Intent(RECEIVE_JSON).putExtra("json", item.toString());
                    LocalBroadcastManager.getInstance(BluetoothDiscoveryService.this).sendBroadcast(deviceInfo);
                } catch(JSONException e) {
                    Log.i(TAG, "onReceive(): Caught JSON Exception");
                }

            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "onReceive(): Restarting BT discovery");
                btAdapter.startDiscovery();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate(): Entered");

        // Ensure service can operate successfully
        if(!hasPermissions()) {
            Log.i(TAG, "onCreate(): does not have all permissions, stopping self");
            stopSelf();
        }

        mLocationDiscovery = new LocationDiscovery();
        mLocationDiscovery.configureLocationClass(this);
        mLocationDiscovery.startLocationUpdates();

        mContext = this;

        FileRW.init(mContext,"bt");

        // Bluetooth discovery setup
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
    }

    /**
     * Handle launching different activities depending on what permissions and
     * capabilities we have.
     * @param intent
     * @param flags
     * @param startId
     * @return START_NOT_STICKY constant to prevent service from restarting when closing app
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        if(START.equals(intent.getAction())) {
            if(!btAdapter.isEnabled()) {
                Log.i(TAG, "onStartCommand(): enabling bluetooth adapter");
                btAdapter.enable();
            }
            Log.i(TAG, "onStartCommand(): Discovering devices");
            btAdapter.startDiscovery();

        } else if(STOP.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): STOP ACTION");
            stopSelf();
        }

        // This prevents the service from restarting after it is killed
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        unregisterReceiver(receiver);
    }

    /**
     * Confirm that service has all permissions required to operate
     * @return true if service should be able to reun without any issues
     */
    private boolean hasPermissions() {
        Log.i(TAG, "hasPermissions()");
        int fineLocationCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(fineLocationCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "hasPermissions(): no FINE LOCATION");
            return false;
        } else if(btAdapter == null) {
            Log.i(TAG, "hasPermissions(): no BT ADAPTER");
            return false;
        } else return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind(): Entered");
        return null;
    }
}
