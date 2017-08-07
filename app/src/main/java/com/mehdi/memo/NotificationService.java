package com.mehdi.memo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationService extends IntentService {

    private static final String LOG_TAG = NotificationService.class.getSimpleName();

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
// The id of the channel.
        String id = "my_channel_01";
// The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
// The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
// Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), id);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());

        }
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle("Notification")
                .setContentText("Content")
                .setSmallIcon(R.drawable.ic_done);
        mNotificationManager.notify(0, builder.build());
        Log.i(LOG_TAG, "USER NOTIFIED!!!");
    }
}