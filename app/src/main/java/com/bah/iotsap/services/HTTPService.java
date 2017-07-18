package com.bah.iotsap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * HTTPService allows us to send information over HTTP.
 * You are able to send files or text over HTTP via POST or form/multipart.
 * TODO: Implement EVERYTHING
 */
public class HTTPService extends Service {

    private static final String TAG  = "HTTPService";
    public  static final String SEND = "com.bah.iotsap.services.HTTPService.SEND";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
