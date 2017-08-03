package com.mehdi.memo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by johndoe on 8/3/17.
 */

public class AlarmFragment extends Fragment {
    //This code is defined and consumed by the app
    public static final int REQUEST_CODE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create an intent
        Intent intent = new Intent(getActivity(), NotificationService.class);
        intent.setAction(NotificationService.ACTION_FIRE_NOTIFICATION);

        //Create a pending intent to be fired outside of the application
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), REQUEST_CODE, intent, 0);

        //Create alarm. We will use the ELAPSED_REALTIME alarm type
        int alarmType = AlarmManager.ELAPSED_REALTIME;
        int interval = 15000; //user-selected interval for alarms
        // TODO: later I will set this value from user preferences

        //Request alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        //Set start delay and interval between repeats
        alarmManager.setRepeating(alarmType,
                SystemClock.elapsedRealtime() + interval,
                interval,
                pendingIntent);


    }

}
