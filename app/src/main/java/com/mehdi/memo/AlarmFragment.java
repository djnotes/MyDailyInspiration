package com.mehdi.memo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by johndoe on 8/3/17.
 */

public class AlarmFragment extends Fragment {

    //Define intervals in milliseconds to use for scheduling
    private static final long INTERVAL_NEVER = -1; //Never
    private static final long INTERVAL_1HOUR = 1000 *  15 * 1; //Todo: Set this to 3600 after testing
    private static final long INTERVAL_2HOURS = 1000 * 3600 * 2;
    private static final long INTERVAL_3HOURS = 1000 * 3600 * 3;
    private static final long INTERVAL_5HOURS = 1000 * 3600 * 5;
    private static final long INTERVAL_6HOURS = 1000 * 3600 * 6;
    private static final long INTERVAL_8HOURS = 1000 * 3600 * 8;
    private static final long INTERVAL_12HOURS = 1000 * 3600 * 12;
    private static final long INTERVAL_24HOURS = 1000 * 3600 * 24;

    //This code is defined and consumed by the app
    private static final int REQUEST_CODE = 0;
    private static final String LOG_TAG = AlarmFragment.class.getSimpleName();
    public final String TAG = this.getClass().getSimpleName();




    public void setupAlarm()
    {
        //Create an intent
        Intent intent = new Intent(getActivity(), NotificationService.class);
        intent.setAction(Const.ACTION_NOTIFY);

        //Create a pending intent to be fired outside of the application
        PendingIntent alarmIntent = PendingIntent.getService(getActivity().getApplicationContext(), REQUEST_CODE, intent, 0);


        //Create alarm. We will use the ELAPSED_REALTIME alarm type
        int alarmType = AlarmManager.ELAPSED_REALTIME;
        long interval; //user-selected interval for alarms; Set this from preferences
        //Instantiate app preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        interval = INTERVAL_NEVER;
        int entryValue = Integer.valueOf(
                prefs.getString(getString(R.string.pref_interval), "0")
        );
        Log.i(LOG_TAG, String.format("Selected Entry: %d",entryValue));
        switch (entryValue) {
            case 1:
                //Give notification every hour
                interval = INTERVAL_1HOUR;
                break;
            case 2:
                interval = INTERVAL_2HOURS;
                break;
            case 3:
                interval = INTERVAL_3HOURS;
                break;
            case 5:
                interval = INTERVAL_5HOURS;
                break;
            case 6:
                interval = INTERVAL_6HOURS;
                break;
            case 8:
                interval = INTERVAL_8HOURS;
                break;
            case 12:
                interval = INTERVAL_12HOURS; //Twice a day
                break;
            case 24:
                interval = INTERVAL_24HOURS; //Once a day
                break;
        }
        Log.i(LOG_TAG, String.format("INTERVAL: %d",interval));


        //Request alarm manager from the system
        if (interval != INTERVAL_NEVER) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            //Set start delay and interval between repeats
            alarmManager.setRepeating(alarmType,
                    SystemClock.elapsedRealtime() + interval,
                    interval,
                    alarmIntent);

        }

        Log.i(TAG, " Alarm set");
    }


//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//         setupAlarm(); //This might not be necessary
//
//        //Todo: INVESTIGATE HOW THE PREFERENCES MUST BE SET ON APP INITIALIZATION AND ON PREFERENCE CHANGE e.g. whether random is on, permanent notification is on, etc.
//        //TODO: Investigate whether we need to setUp alarm again if the user has not changed intervals when changing preferences
//    }
}
