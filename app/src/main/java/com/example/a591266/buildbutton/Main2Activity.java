package com.example.a591266.buildbutton;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Main2Activity extends AppCompatActivity {

    //calling this string whatever we want
    private static final String CONTENT_MIME_TYPE = "com.example.a591266.buildbutton/test";

    private static final String TAG = "Main2Activity";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter writeTagFilters[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            //Finish making this button
            //Button nfc_start = (Button)
        });
        //making Back Button
        Button back_btn = (Button) findViewById(R.id.backb_readpage);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainpage = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(mainpage);
            }
        });


        //read the android doc
        //NdefMessage message;
        //message.

        //Getting the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Log.i(TAG, "onCreate(): no nfcAdapter available");
            Toast.makeText(this, "No NFCAdapter available", Toast.LENGTH_SHORT).show();
            finish();
        }
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.i(TAG, "onCreate\n");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //NfcAdapter nfcAdapter;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        Log.i(TAG, "onResume\n");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //NfcAdapter nfcAdapter;
        nfcAdapter.disableForegroundDispatch(this);
        Log.i(TAG, "onPause\n");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent(): action = " + intent.getAction());
        //Log.i(TAG, "onNewIntent\n");

        /*
        Trying to
        1) Declare an Intent Filter to announce to the system that it’s enabled to work on NFC.
        2) Have a method that Android will call when NFC is detected.
        3) Create a method to build a NDEF message.
        4) Create a method to write the NDEF message.

        Creating an intent filter

        JSONObject
*/
//        IntentFilter tagDetected = new IntentFilter(nfcAdapter.ACTION_TAG_DISCOVERED);
//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//
//        //Checks for the ACTION_NDEF_DISCOVERED intent and gets the NDEF messages from an intent extra
//        if (intent != null && nfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
//        {
//            Log.i(TAG, "Did not find NDEF message");
//            Parcelable [] rawMessages =
//                    intent.getParcelableArrayExtra(nfcAdapter.EXTRA_NDEF_MESSAGES);
//
//        if (nfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
//        {
//            Log.i(TAG, "this intent has getAction");
//        }
//            if (rawMessages != null)
//            {
//                Log.i(TAG, "Found an NDEF message");
//                NdefMessage [] messages = new NdefMessage[rawMessages.length];
//                for (int counter = 0; counter < rawMessages.length; counter++)
//                {
//                    messages[counter] = (NdefMessage) rawMessages[counter];
//                    Log.i(TAG, messages[counter].toString());
//                }
//                //Process the message array
//            }
//        }

        //String cat = null;
        //cat.equals("cat");

        Tag tag_ = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord rec = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                CONTENT_MIME_TYPE.getBytes(StandardCharsets.US_ASCII),
                new byte[0],
                "cat".getBytes(StandardCharsets.US_ASCII));
        NdefMessage msg = new NdefMessage(rec);

        Ndef ndef = Ndef.get(tag_);
        Log.i(TAG, "onNewIntent(): isWritable = " + ndef.isWritable());
        try
        {
            ndef.connect();
            Log.i(TAG, "onNewIntent(): isConnected() = " + ndef.isConnected());
            ndef.writeNdefMessage(msg);
            ndef.close();

        }
        catch (Exception e)
        {
            Log.i(TAG, "onNewIntent(): Caught exception");
        }
        finish();
    }

}



/*
Creating NFC Reader
Adding base class of Activity
Functions from https://github.com/nadam/nfc-reader/blob/master/libs/guavalib.jar
 */

