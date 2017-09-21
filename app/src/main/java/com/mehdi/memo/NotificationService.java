package com.mehdi.memo;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mehdi.memo.data.MemoContract.MemoEntry;

import java.util.Random;


public class NotificationService extends IntentService {

    private static final String LOG_TAG = NotificationService.class.getSimpleName();
    private static final int REQUEST_CODE = 100;

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    private String mContentText; //Body of selected text
    private String mTitle;  //Title of the selected text
    private Uri mUri; //Uri for selected note

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Define an intent for future
        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                REQUEST_CODE,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        //Get message and title to use for the dialog. Also get Uri in case we use it in future
        initializeNotifyInfo();
        int notifyId = 1;
// The id of the channel.
        String id = "my_channel_01";
// The user-visible name of the channel.
        CharSequence name = Const.DEFAULT_NOTIFY_CHANNEL;
// The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(getApplicationContext(), id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
// Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
        builder.addAction(R.drawable.ic_done, "Action Title", PendingIntent.getActivity(
                getApplicationContext(), REQUEST_CODE + 1,
                new Intent (getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        ));
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle(mTitle)
                .setContentText(mContentText)
                .setSmallIcon(R.drawable.ic_done)
                .setLights(Color.argb(0, 255, 0, 0), 1000, 3000)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        mNotificationManager.notify(notifyId, builder.build());
        Log.i(LOG_TAG, "USER NOTIFIED!!!");
    }

    private void initializeNotifyInfo() {
        //Check preferences to see whether random inspiration is ON.
        //In that case pick a random note, else return the latest
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_filename), Context.MODE_PRIVATE);
        boolean randomNote = preferences.getBoolean(getString(R.string.pref_random), false);

        //It appears I need to query the whole database into a cursor and then pick one!
        //Create a projection
        String[] projection = {
                MemoEntry._ID,
                MemoEntry.COLUMN_MEMO_NOTE,
                MemoEntry.COLUMN_MEMO_TITLE
        };
        Cursor cursor = getContentResolver().query(
                MemoEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        ); //Cursor to hold the query result
        if (cursor != null) {
            cursor.moveToFirst(); //not doing this is apparently always a culprit
        }
        int count = 0; //size of cursor
        if (cursor != null) {
            count = cursor.getCount();
        }
        int selectRow = count; //selected row from the result set
        Log.d(LOG_TAG, "NUMBER OF ROWS: " + String.valueOf(count));

        if (count == 0) {
            mContentText = getString(R.string.please_add_some_notes);
            mTitle = getString(R.string.no_notes_available);
            mUri = null;
            return; //If nothing in the database, return
        } else {
            if (randomNote) { //pick a random inspiration
                Random random = new Random();
                selectRow = random.nextInt(count); //this gives random number between 0 (inclusive) and count (exclusive)
                Log.d(LOG_TAG,"Selected Row: " + String.valueOf(selectRow));
            }
            else { //If random note is not enabled, then pick the last one. Note: Index start from zero so we have to do minus 1
                selectRow = count - 1;
            }
        }
        cursor.move(selectRow);
        mTitle = cursor.getString(
                cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_TITLE)
        );
        mContentText = cursor.getString(
                cursor.getColumnIndexOrThrow(MemoEntry.COLUMN_MEMO_NOTE)
        );
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(MemoEntry._ID)
        );
        mUri = ContentUris.withAppendedId(MemoEntry.CONTENT_URI, id);
    }
}