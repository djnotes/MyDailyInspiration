package com.mehdi.memo;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_NOTIFICATION_ENABLE = "pref_enable_notification";
    public static final String KEY_RANDOM_NOTIFICATION = "pref_toggle_random";
    public static final String KEY_ONGOING_NOTIFICATION = "pref_toggle_ongoing";
    public static final String KEY_SHOW_0N_LOCKSCREEN = "pref_show_on_lockscreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

}