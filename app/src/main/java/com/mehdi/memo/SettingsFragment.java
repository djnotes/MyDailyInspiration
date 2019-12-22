package com.mehdi.memo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

/**
 * Created by john on 6/30/17.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();
    private ListPreference mListPref;
    private EditTextPreference mNamePref;
    private EditTextPreference mMottoPref;

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

        //TODO: Find and set summary for the name and motto preference fields
        mNamePref = (EditTextPreference) findPreference(getString(R.string.key_pref_name));
        mMottoPref = (EditTextPreference) findPreference(getString(R.string.key_pref_motto));

        mNamePref.setSummary(mNamePref.getText());
        mMottoPref.setSummary(mMottoPref.getText());
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        CharSequence entry= mListPref.getEntry();
        mListPref.setSummary(entry);

        //Also update name and motto when preference changed
        mNamePref.setSummary(mNamePref.getText());
        mMottoPref.setSummary(mMottoPref.getText());
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
