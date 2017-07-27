package com.bah.iotsap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bah.iotsap.util.FileRW;

public class SendFileService extends Service {

    private static final String TAG = "SendFileService";
    private final int PERIODIC_EVENT_TIMEOUT = 30000;

    private Handler mHandler;
    private Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "OnCreate()");
        mContext = this;
        mHandler = new Handler();
        mHandler.postDelayed(sendFiles, PERIODIC_EVENT_TIMEOUT);
    }

    private Runnable sendFiles = new Runnable() {
        @Override
        public void run() {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.i(TAG, "run: Starting HttpService for BT");
                    Intent bt = new Intent(HttpService.SEND_FILE, null,
                            getApplicationContext(), HttpService.class);
                    bt.putExtra("title", "bt");
                    startService(bt);
                    LocalBroadcastManager.getInstance(SendFileService.this).sendBroadcast(bt);
                }
            }, 5000);

            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.i(TAG, "run: Starting HttpService for BLE");
                    Intent ble = new Intent(HttpService.SEND_FILE, null,
                            getApplicationContext(), HttpService.class);
                    ble.putExtra("title", "ble");
                    startService(ble);
                    LocalBroadcastManager.getInstance(SendFileService.this).sendBroadcast(ble);
                }
            }, 15000);

            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.i(TAG, "run: Starting HttpService for BEACON");
                    Intent beacon = new Intent(HttpService.SEND_FILE, null,
                            getApplicationContext(), HttpService.class);
                    beacon.putExtra("title", "beacon");
                    startService(beacon);
                    LocalBroadcastManager.getInstance(SendFileService.this).sendBroadcast(beacon);
                }
            }, 25000);

//            Intent nfc = new Intent(HttpService.SEND_FILE, null,
//                    getApplicationContext(), HttpService.class);
//            nfc.putExtra("title", nfc);
//            startService(nfc);
//
//            Intent rfid = new Intent(HttpService.SEND_FILE, null,
//                    getApplicationContext(), HttpService.class);
//            rfid.putExtra("title", rfid);
//            startService(rfid);

            FileRW.delete(mContext, "bt");
            FileRW.init(mContext,"bt");
            FileRW.delete(mContext, "ble");
            FileRW.init(mContext, "ble");
            FileRW.delete(mContext, "beacon");
            FileRW.init(mContext, "beacon");

            Log.i(TAG, "30 seconds have passed");
            mHandler.postDelayed(sendFiles, PERIODIC_EVENT_TIMEOUT);
        }
    };

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mHandler.removeCallbacks(sendFiles);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
