package com.mehdi.memo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    public static final int REQUEST_CODE=100;

    private String mTitle;      //Title for notification
    private String mContentText; //Content text for the notification

    private boolean mNotificationEnabled; //is notification enabled?
    private boolean mRandomNotificationEnabled; //showing user preference for receiving random notifications
    private boolean mOnGoingNotification; //showing that the notification cannot be dismissed by clicking on it
    private boolean mShowOnLockScreen; //Can we show the notification on the lock screen

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Read User preferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_NOTIFICATION_ENABLE,false);
        mRandomNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_RANDOM_NOTIFICATION,true);
        mOnGoingNotification = prefs.getBoolean(SettingsActivity.KEY_ONGOING_NOTIFICATION,false);
        mShowOnLockScreen = prefs.getBoolean(SettingsActivity.KEY_SHOW_0N_LOCKSCREEN,true);

        //Create a URI to send to EditorActivity
        Uri uri = ContentUris.withAppendedId(MemoContract.MemoEntry.CONTENT_URI,0);


        //Create a PendingIntent for our notification
        Intent targetIntent=new Intent(this,EditorActivity.class);
        targetIntent.setData(uri);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,REQUEST_CODE,targetIntent,PendingIntent.FLAG_ONE_SHOT);

        //Check to see if settings allow notifications, then send one if true
        if(mNotificationEnabled) {
            String message=intent.getStringExtra("MESSAGE");
            NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.alert);
            builder.setContentTitle("Hi There!");
            builder.setContentText(message);
            builder.setContentIntent(pendingIntent);
            if(mOnGoingNotification) {
                builder.setOngoing(true);
            }
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,builder.build());
        }



    }
}
