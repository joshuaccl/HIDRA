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


public class Main3Activity extends AppCompatActivity {

    public static final String CONTENT_MIME_TYPE = "com.bah.buildbutton/test";
    private static final String TAG = "Main3Activity";
    private NfcAdapter mAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null)
        {
            Log.i(TAG, "onCreate(): no nfcAdapter available");
            Toast.makeText(this, "no NFCAdapter available", Toast.LENGTH_SHORT).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(mAdapter.ACTION_TAG_DISCOVERED);

        //How to
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
