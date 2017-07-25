package com.bah.iotsap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.bah.iotsap.util.FileRW;

public class SendFileService extends Service {

    private Handler mHandler;
    private Context mContext;

    private final int PERIODIC_EVENT_TIMEOUT = 30000;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mHandler = new Handler();
        mHandler.postDelayed(sendFiles, PERIODIC_EVENT_TIMEOUT);
    }

    private Runnable sendFiles = new Runnable() {
        @Override
        public void run() {

            Intent bt = new Intent(HttpService.SEND_FILE, null,
                    getApplicationContext(), HttpService.class);
            bt.putExtra("FILENAME", bt);
            startService(bt);

            Intent ble = new Intent(HttpService.SEND_FILE, null,
                    getApplicationContext(), HttpService.class);
            ble.putExtra("FILENAME", ble);
            startService(ble);

//            Intent nfc = new Intent(HttpService.SEND_FILE, null,
//                    getApplicationContext(), HttpService.class);
//            nfc.putExtra("FILENAME", nfc);
//            startService(nfc);
//
//            Intent rfid = new Intent(HttpService.SEND_FILE, null,
//                    getApplicationContext(), HttpService.class);
//            rfid.putExtra("FILENAME", rfid);
//            startService(rfid);

            Intent beacon = new Intent(HttpService.SEND_FILE, null,
                    getApplicationContext(), HttpService.class);
            beacon.putExtra("FILENAME", beacon);
            startService(beacon);

            FileRW.delete(mContext, bt.toString());
            FileRW.delete(mContext, ble.toString());
//            FileRW.delete(mContext, nfc.toString());
//            FileRW.delete(mContext, rfid.toString());
            FileRW.delete(mContext, beacon.toString());

            Toast.makeText(SendFileService.this, "30 seconds have passed", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(sendFiles, PERIODIC_EVENT_TIMEOUT);
        }
    };

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(sendFiles);
        super.onDestroy();
    }

    public SendFileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
