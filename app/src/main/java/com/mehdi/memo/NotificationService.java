package com.mehdi.memo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.mehdi.memo.R;
import com.mehdi.memo.data.MemoContract;

/**
 * Created by john on 6/29/17.
 */

public class NotificationService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public NotificationService() {
        super("NotificationService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Read User preferences. If notification is enabled, then send a notification
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationState=prefs.getBoolean(SettingsActivity.KEY_NOTIFICATION_ENABLE,false);
        if(notificationState) {
            String message=intent.getStringExtra("MESSAGE");
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.alert);
            builder.setContentTitle("Hi There!");
            builder.setContentText(message);
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
        }



    }
}
