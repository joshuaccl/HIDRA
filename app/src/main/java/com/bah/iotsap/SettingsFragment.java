package com.bah.iotsap;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingsFragment";

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
