package com.bah.nfctest;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate()");

        // Set up NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if(nfcAdapter == null) {
            Log.i(TAG, "onCreate(): nfcAdapter is null");
            Toast.makeText(this, "No NFC capability", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(!nfcAdapter.isEnabled()) {
            Log.i(TAG, "onCreate(): nfcAdapter is not enabled");
            Toast.makeText(this, "Please enable NFC adapter", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up UI elements
        button   = (Button)   findViewById(R.id.write_button);
        textView = (TextView) findViewById(R.id.text_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "button onClick()");
                startActivity(new Intent(getApplicationContext(), WriteActivity.class));
            }
        });
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
        if(intent == null) {
            Log.i(TAG, "onNewIntent(): NULL INTENT");
            return;
        }

        Log.i(TAG, "onNewIntent(): action = " + intent.getAction() + ", Type = " + intent.getType());
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i(TAG, "onNewIntent(): tag to string: " + tag.toString());

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for(int i = 0; i < msgs.length; ++i) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    Log.i(TAG, "onNewIntent(): Message" + i + " = " + msgs[i].toString());
                }
            }
        }
    }
}
