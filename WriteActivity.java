package com.bah.nfctest;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class WriteActivity extends AppCompatActivity {

    public  static final String CONTENT_MIME_TYPE = "com.bah.nfctest/test";
    private static final String TAG = "WriteActivity";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null) {
            Log.i(TAG, "onCreate(): no nfcAdapter available");
            Toast.makeText(this, "No NFCAdapter available", Toast.LENGTH_SHORT).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent(): action = " + intent.getAction());

        String hello = null;
        hello.equals("hello");

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord rec = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                CONTENT_MIME_TYPE.getBytes(StandardCharsets.US_ASCII),
                new byte[0],
                "hello".getBytes(StandardCharsets.US_ASCII));
        NdefMessage msg = new NdefMessage(rec);

        Ndef ndef = Ndef.get(tag);
        Log.i(TAG, "onNewIntent(): isWritable = " + ndef.isWritable());
        try {
            ndef.connect();
            Log.i(TAG, "onNewIntent(): isConnected() = " + ndef.isConnected());
            ndef.writeNdefMessage(msg);
            ndef.close();
        } catch(Exception e) {
            Log.i(TAG, "onNewIntent(): Caught exception");
        }
        finish();
    }
}
