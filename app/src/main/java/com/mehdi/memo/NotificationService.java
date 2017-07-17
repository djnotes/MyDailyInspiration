package com.mehdi.memo;

import android.app.IntentService;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.mehdi.memo.R;
import com.mehdi.memo.data.MemoContract.MemoEntry;

import java.util.Random;


/**
 * Created by john on 6/29/17.
 */

public class NotificationService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {
        super("NotificationService");
    }

    public static final int REQUEST_CODE = 100;

    private String mTitle;      //Title for notification
    private String mContentText; //Content text for the notification
    private int mRowID = -1; //ID of the row used to create URI. Initialized to -1 to prevent Exceptions in case
    //It does not get initialized in the conditions below

    private boolean mNotificationEnabled; //is notification enabled?
    private boolean mRandomNotificationEnabled; //showing user preference for receiving random notifications
    private boolean mOnGoingNotification; //showing that the notification cannot be dismissed by clicking on it
    private boolean mShowOnLockScreen; //Can we show the notification on the lock screen
    private Cursor mCursor;  //This will hold the result of our query


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Read User preferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_NOTIFICATION_ENABLE, false);
        mRandomNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_RANDOM_NOTIFICATION, true);
        mOnGoingNotification = prefs.getBoolean(SettingsActivity.KEY_ONGOING_NOTIFICATION, false);
        mShowOnLockScreen = prefs.getBoolean(SettingsActivity.KEY_SHOW_0N_LOCKSCREEN, true);

        //If notification is disabled, do not bother
        if (!mNotificationEnabled) {
            return;
        }


        //Define a projection for queries
        String[] projection =
                {
                        MemoEntry._ID,
                        MemoEntry.COLUMN_MEMO_TITLE,
                        MemoEntry.COLUMN_MEMO_NOTE
                };

        //First query the whole database with our projection,
        //Then either send a random row (if random enabled),
        //Or send the last row if random not enabled.
        mCursor = getContentResolver().query(MemoEntry.CONTENT_URI, projection, null, null, null);

        //TODO: SET UP A SETTINGS HEADER AND ADD A TIME PICKER AND A PLUS SIGN TO PROVIDE MORE TIMES (AS IN THE ALARM APP)



        prepareAndShowNotification();


    }

    private void prepareAndShowNotification() {
        int count = mCursor.getCount();


        if(count == 0) {
            mTitle = getString(R.string.no_notes_available);
            mContentText = getString(R.string.please_add_some_notes);
        }

        //Integer variables to hold column indexes
        int idColumnIndex;
        int titleColumnIndex;
        int contentColumnIndex;

        //Get column indexes which are of interest to us
        titleColumnIndex = mCursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_TITLE);
        contentColumnIndex = mCursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_NOTE);
        idColumnIndex = mCursor.getColumnIndexOrThrow(MemoEntry._ID);


        //If will not have random inspirations for zero or 1 item
        if (mRandomNotificationEnabled && count >=1) {
            Random rand=new Random();
            int randomRow = rand.nextInt(count);
            mCursor.move(randomRow); //Move to our randomRow

            idColumnIndex = mCursor.getColumnIndexOrThrow(MemoEntry._ID);

            mTitle = mCursor.getString(titleColumnIndex);
            mContentText = mCursor.getString(contentColumnIndex);
            mRowID = mCursor.getInt(idColumnIndex);
        }
        else if (!mRandomNotificationEnabled && count >= 1){ //If random is off and cursor has more than 1 item
            mCursor.moveToLast(); //Go to the last item

            mTitle = mCursor.getString(titleColumnIndex);
            mContentText = mCursor.getString(contentColumnIndex);
            mRowID = mCursor.getInt(idColumnIndex);
        }


        //Create a URI to send to EditorActivity
        Uri uri = ContentUris.withAppendedId(MemoEntry.CONTENT_URI, mRowID);

        //TODO: SET DEFAULT NOTIFICATION SOUND AND ADD OPTION IN SETTINGS PAGE FOR CHANGING DEFAULT SOUND


        //Create a PendingIntent for our notification
        Intent targetIntent = new Intent(this, EditorActivity.class);
        targetIntent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, targetIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(mTitle);
        builder.setContentText(mContentText);
        builder.setSmallIcon(R.drawable.alert);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        if (mRowID != -1) { //Unless we have a valid ID, do not set an intent
            builder.setContentIntent(pendingIntent);
        }


        //Check to see if settings allow notifications, then send one if true
        if (mNotificationEnabled) {
            if (mOnGoingNotification) {
                builder.setOngoing(true);
            } else {
                builder.setOngoing(false);
            }
            //Lock screen notification is supported only on Android 5 onwards
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mShowOnLockScreen) {
                    builder.setVisibility(Notification.VISIBILITY_PUBLIC);
                } else {
                    builder.setVisibility(Notification.VISIBILITY_PRIVATE);
                }

            }

        }

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());

    }


}

