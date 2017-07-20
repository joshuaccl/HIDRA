package com.bah.iotsap;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * SettingsFragment is what is used to change any sharedPreference for the entire app.
 * It allows users to change persistent settings. The settings are defined in preferences.xml.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingsFragment";
    // Strings representing the possible setting options.
    public  static final String PREF_BT_SERVICE  = "settings_bluetooth_service";
    public  static final String PREF_BLE_SERVICE = "settings_ble_service";
    public  static final String PREF_NFC_SERVICE = "settings_nfc_service";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: entered");
        /**
         * This fragment is populated from the preferences.xml file located in the
         * xml resource folder. The PreferenceFragment class handles the appearance.
         * All I have to do is describe the preference options in the xml file.
         */
        addPreferencesFromResource(R.xml.preferences);
        Log.i(TAG, "onCreate: finished addPreferencesFromResource");
    }
}
