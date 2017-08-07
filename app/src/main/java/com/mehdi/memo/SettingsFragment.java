package com.mehdi.memo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by john on 6/30/17.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();
    private ListPreference mListPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Load the preferences from XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
    @Override
    public void onStart() {
        super.onStart();
        //Find and initialize list preference
        mListPref= (ListPreference) findPreference(getString(R.string.pref_interval));
        CharSequence entry = mListPref.getEntry();
        mListPref.setSummary(entry);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        CharSequence entry= mListPref.getEntry();
        mListPref.setSummary(entry);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
