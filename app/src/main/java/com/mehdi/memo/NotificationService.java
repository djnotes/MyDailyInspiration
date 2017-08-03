package com.mehdi.memo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.mehdi.memo.data.MemoContract;

import java.util.Random;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {


    public static final String ACTION_FIRE_NOTIFICATION = "com.mehdi.memo.action.ACTION_FIRE_NOTIFICATION";
    public static final String ACTION_BAZ = "com.mehdi.memo.action.BAZ";

    public static final int REQUEST_CODE = 1;


    private static final long DELAY = 3000;
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
    public void onCreate() {
        //Define a projection for queries
        String[] projection =
                {
                        MemoContract.MemoEntry._ID,
                        MemoContract.MemoEntry.COLUMN_MEMO_TITLE,
                        MemoContract.MemoEntry.COLUMN_MEMO_NOTE
                };

        //First query the whole database with our projection,
        //Then either send a random row (if random enabled),
        //Or send the last row if random not enabled.
        mCursor = getApplicationContext().getContentResolver().query(MemoContract.MemoEntry.CONTENT_URI, projection, null, null, null);
        mCursor.moveToFirst();

        //Read User preferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_NOTIFICATION_ENABLE, false);
        mRandomNotificationEnabled = prefs.getBoolean(SettingsActivity.KEY_RANDOM_NOTIFICATION, true);
        mOnGoingNotification = prefs.getBoolean(SettingsActivity.KEY_ONGOING_NOTIFICATION, false);
        mShowOnLockScreen = prefs.getBoolean(SettingsActivity.KEY_SHOW_0N_LOCKSCREEN, true);

        //If notification is disabled, do not bother
//        if (!mNotificationEnabled) {
//            return;
//        }
        prepareAndShowNotification();

        super.onCreate();
    }

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.mehdi.memo.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.mehdi.memo.extra.PARAM2";

    //Define constant extras to use for sending message to DialogService
    private static final String EXTRA_DIALOG_TITLE = "com.mehdi.memo.extra.dialog_title";
    private static final String EXTRA_DIALOG_MESSAGE = "com.mehdi.memo.extra.dialog_message";
    private static final String EXTRA_DIALOG_URI = "com.mehdi.memo.extra.dialog_uri";

    public NotificationService() {
        super("NotificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFireNotification(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_FIRE_NOTIFICATION);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FIRE_NOTIFICATION.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFireNotification(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            } else {
                throw new IllegalArgumentException("Illegal action");
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFireNotification(String param1, String param2) {
        prepareAndShowNotification();

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void prepareAndShowNotification() {
        int count = mCursor.getCount();
        if (count == 0) {
            mTitle = getString(R.string.no_notes_available);
            mContentText = getString(R.string.please_add_some_notes);
            return;
        }

        //Integer variables to hold column indexes
        int idColumnIndex;
        int titleColumnIndex;
        int contentColumnIndex;

        //Get column indexes which are of interest to us
        titleColumnIndex = mCursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_MEMO_TITLE);
        contentColumnIndex = mCursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_MEMO_NOTE);
        idColumnIndex = mCursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID);


        //If will not have random inspirations for zero or 1 item
        if (mRandomNotificationEnabled && count >= 1) {
            Random rand = new Random();
            int randomRow = rand.nextInt(count);
            mCursor.move(randomRow); //Move to our randomRow

            idColumnIndex = mCursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID);

            mTitle = mCursor.getString(titleColumnIndex);
            mContentText = mCursor.getString(contentColumnIndex);
            mRowID = mCursor.getInt(idColumnIndex);
        } else if (!mRandomNotificationEnabled && count >= 1) { //If random is off and cursor has more than 1 item
            mCursor.moveToLast(); //Go to the last item

            mTitle = mCursor.getString(titleColumnIndex);
            mContentText = mCursor.getString(contentColumnIndex);
            mRowID = mCursor.getInt(idColumnIndex);
        }


        //Create a URI to send to DialogService
        Uri uri = ContentUris.withAppendedId(MemoContract.MemoEntry.CONTENT_URI, mRowID);

        //Create a DialogService to pass to addAction
        Intent dialogIntent = new Intent(getApplicationContext(), DialogService.class);

        //Add extras to dialogIntent
        dialogIntent.putExtra(this.EXTRA_DIALOG_TITLE,mTitle)
                .putExtra(this.EXTRA_DIALOG_MESSAGE, mContentText)
                .putExtra(this.EXTRA_DIALOG_URI , uri);

        PendingIntent pendingDialogIntent = PendingIntent.getActivity(getApplicationContext(),
                REQUEST_CODE,
                dialogIntent,
                0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(mTitle)
                .setContentText(mContentText)
                .setSmallIcon(R.drawable.alert)
                .setSound(Settings.System.DEFAULT_RINGTONE_URI)
                .addAction(R.drawable.bell,
                        getString(R.string.title_view),
                        pendingDialogIntent);
        if (mRowID != -1) { //Unless we have a valid ID, do not set an intent
//            buildAndShowDialog();
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

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }


}
