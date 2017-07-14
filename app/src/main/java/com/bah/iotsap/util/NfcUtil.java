package com.bah.iotsap.util;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * NfcUtil is a class that provides several static methods to make parsing and writing
 * from and to NFC tags easier.
 * This class cannot be instantiated.
 * TODO: Finish methods for parsing and returning NFC messages and Records
 */
public final class NfcUtil {

    private static final String TAG = "NfcUtil";

    // GET NdefMessage[] from a tag
    public static final NdefMessage[] getNdefMessages(Tag tag) {
        NdefMessage[] messages = null;
        Ndef ndef = Ndef.get(tag);
        try {
            messages = new NdefMessage[]{ndef.getNdefMessage()};
            Log.i(TAG, "gettNdefMessages(Tag): message[] length = " + messages.length);
        } catch(FormatException e) {
            Log.i(TAG, "getNdefMessages(Tag): Caught Format Exception");
        } catch(IOException e) {
            Log.i(TAG, "getNdefMessages(Tag): Caught IOException");
        }
        return messages;
    }

    // GET NdefMessage[] from an intent
    public static final NdefMessage[] getNdefMessages(Intent intent) {
        NdefMessage[] messages = null;

        try {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for(int i = 0; i < rawMsgs.length; ++i) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                    Log.i(TAG, "getNdefMessages(Intent): msgs.length = " + messages.length);
                    Log.i(TAG, "getNdefMessages(Intent): Msg" + i + ": " + messages[i].toString());
                }
            }
        } catch(NullPointerException e) {
            Log.i(TAG, "getNdefMessages(Intent): Caught NullPointerException");
        } catch(Exception e) {
            Log.i(TAG, "getNdefMessages(Intent): Caught Exception");
        }
        return messages;
    }

    // GET NdefRecord[] from a Tag
    public static final NdefRecord[] getNdefRecords(Tag tag) {
        NdefRecord[] records = null;

        try {
            Ndef ndef = Ndef.get(tag);
            records = ndef.getNdefMessage().getRecords();
            Log.i(TAG, "getNdefRecords(Tag): records.length = " + records.length);
        } catch(NullPointerException e) {
            Log.i(TAG, "getNdefRecords(Tag): Caught NullPointerException");
        }catch(Exception e) {
            Log.i(TAG, "getNdefRecords(Tag): Caught Exception");
        }
        return records;
    }

    // GET NdefRecord[] from all messages from an intent
    public static final NdefRecord[] getNdefRecords(Intent intent) {
        NdefRecord[] records = null;
        ArrayList<NdefRecord> recordList = new ArrayList<>();

        try {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for(int i = 0; i < rawMsgs.length; ++i) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    for(NdefRecord rec : msgs[i].getRecords()) {
                        recordList.add(rec);
                    }
                }
                records = new NdefRecord[recordList.size()];
                records = recordList.toArray(records);
            }
        } catch(NullPointerException e) {
            Log.i(TAG, "getNdefMessages(Intent): Caught NullPointerException");
        } catch(Exception e) {
            Log.i(TAG, "getNdefMessages(Intent): Caught Exception");
        }
        return records;
    }

    // WRITE TODO: method
    public static final boolean writeMessage(Tag tag, NdefMessage msg) {

        return true;
    }
}
