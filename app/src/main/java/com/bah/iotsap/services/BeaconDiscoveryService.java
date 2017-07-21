package com.bah.iotsap.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bah.iotsap.util.LocationDiscovery;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.utils.MacAddress;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.mgmtsdk.connection.api.DeviceConnectionProvider;
import com.estimote.coresdk.recognition.packets.Beacon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BeaconDiscoveryService extends Service {

    private static final String TAG = "BeaconDiscoveryService";
    // Intent action strings
    public static final String RECEIVE_JSON = "com.bah.iotsap.services.BeaconDiscoveryService.RECEIVE_JSON";
    public static final String START = "com.bah.iotsap.services.BeaconDiscoveryService.START";
    public static final String STOP  = "com.bah.iotsap.services.BeaconDiscoveryService.STOP";
    // SharedPreferences strings
    public static final String PREF_BEACON_SERVICE = "pref_beacon_service";

    private ArrayList<Beacon> beaconArrayList;
    private BeaconManager beaconManager;
    private DeviceConnectionProvider connectionProvider;
    private BeaconRegion beacons;
    private LocationDiscovery mLocationDiscovery;


    public BeaconDiscoveryService() {
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate(): Entered");

        //Initialize Location Discovery Object
        mLocationDiscovery = new LocationDiscovery();
        mLocationDiscovery.configureLocationClass(this);
        mLocationDiscovery.startLocationUpdates();

        beacons = new BeaconRegion(
                "monitored region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                null , null
        );
        beaconArrayList = new ArrayList<>();
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, final List<Beacon> rangedBeacons) {
                if(rangedBeacons.size() > 0) {
                    for (Beacon beacon : rangedBeacons) {
                        beaconArrayList.add(beacon);
                        final String deviceName = ("iBeacon: " + beacon.getUniqueKey());
                        final MacAddress macAddress = beacon.getMacAddress();
                        final Calendar time = Calendar.getInstance();
                        final String timeStamp = time.getTime().toString();
                        final Integer rssi = beacon.getRssi();
                        final Location location = mLocationDiscovery.getLocation();

                        try {
                            JSONObject info = new JSONObject();
                            info.put("date", timeStamp);
                            info.put("mac", macAddress);
                            info.put("name", deviceName);
                            info.put("rssi", rssi);
                            info.put("latitude", location.getLatitude());
                            info.put("longitude", location.getLongitude());
                            info.put("altitude", location.getAltitude());
                            Log.d(TAG, info.toString());
                            sendMessageToActivity(info.toString());
                        } catch (JSONException e) {

                        }
                    }
                }
            }
        });



//        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
//                                                @Override
//                                                public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> list) {
//                                                        for (Beacon beacon : list) {
//                                                            beaconArrayList.add(beacon);
//                                                            final String deviceName = ("iBeacon: " + beacon.getUniqueKey());
//                                                            final MacAddress macAddress = beacon.getMacAddress();
//                                                            final Calendar time = Calendar.getInstance();
//                                                            final String timeStamp = time.getTime().toString();
//                                                            final Integer rssi = beacon.getRssi();
//
//                                                            try {
//                                                            JSONObject info = new JSONObject();
//                                                            info.put("date", timeStamp);
//                                                            info.put("mac", macAddress);
//                                                            info.put("name", deviceName);
//                                                            info.put("rssi", rssi);
//                                                            Log.d("Service:", info.toString());
//                                                            sendMessageToActivity(info.toString());
//                                                             } catch (JSONException e) {
//
//                                                         }
//                                                        }
//                                                }
//
//                                                @Override
//                                                public void onExitedRegion(BeaconRegion beaconRegion) {
//
//                                                }
//
//                                            });
        connectionProvider = new DeviceConnectionProvider(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
//        beaconManager.setForegroundScanPeriod(2000,2000);
//        beaconManager.setBackgroundScanPeriod(10000,5000);
        if(START.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): Entered START action");

            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.setForegroundScanPeriod(5000, 5000);
//                beaconManager.startRanging(new BeaconRegion(
//                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
//                        47985, 43250
//                ));
//                beaconManager.startMonitoring(new BeaconRegion(
//                        "monitored region",
//                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
//                        32436, 26381
//                ));
                    beaconManager.startMonitoring(beacons);
                    beaconManager.startRanging(beacons);
                }
            });

//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//                                  @Override
//                                  public void onServiceReady() {
//
//                                      beaconManager.setConfigurableDevicesListener(new BeaconManager.ConfigurableDevicesListener() {
//                                          @Override
//                                          public void onConfigurableDevicesFound(List<ConfigurableDevice> configurableDevices) {
//                                              for (ConfigurableDevice configurableDevice : configurableDevices) {
//                                                  device = configurableDevice;
//                                                  beaconArrayList.add(device);
//                                                  final String deviceName = ("iBeacon: " + device.getUniqueKey());
//                                                  final MacAddress macAddress = device.macAddress;
//                                                  final Long time = device.discoveryTime;
//                                                  final Integer rssi = device.rssi;
//
//                                                  try {
//                                                      JSONObject info = new JSONObject();
//                                                      info.put("date", time);
//                                                      info.put("mac", macAddress);
//                                                      info.put("name", deviceName);
//                                                      info.put("rssi", rssi);
//                                                      Log.d("Service:", info.toString());
//                                                      sendMessageToActivity(info.toString());
//                                                  } catch (JSONException e) {
//
//                                                  }
//
//                                                  connectionProvider.connectToService(new DeviceConnectionProvider.ConnectionProviderCallback() {
//                                                      @Override
//                                                      public void onConnectedToService() {
//                                                          connection = connectionProvider.getConnection(device);
//                                                          connection.connect(new DeviceConnectionCallback() {
//                                                              @Override
//                                                              public void onConnected() {
//
//                                                              }
//
//                                                              @Override
//                                                              public void onDisconnected() {
//                                                                  beaconArrayList.remove(device);
//                                                                  Intent intent = new Intent("Lost");
//                                                                  intent.putExtra("Device", device.macAddress.toString());
//                                                                  LocalBroadcastManager.getInstance(BeaconDiscoveryService.this).sendBroadcast(intent);
//
//                                                              }
//
//                                                              @Override
//                                                              public void onConnectionFailed(DeviceConnectionException e) {
//
//                                                              }
//                                                          });
//                                                      }
//                                                  });
//                                              }
//                                          }
//                                      });
//                                  }
//                              });
//        beaconManager.startConfigurableDevicesDiscovery();
        }
        else if(STOP.equals(intent.getAction())) {
            Log.i(TAG, "onStartCommand(): Entered STOP action");
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        connectionProvider.destroy();
        beaconArrayList.clear();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessageToActivity(String device) {
        Intent intent = new Intent(RECEIVE_JSON).putExtra("json", device);
        Log.d("Beacon: ",device);
        LocalBroadcastManager.getInstance(BeaconDiscoveryService.this).sendBroadcast(intent);
    }
}