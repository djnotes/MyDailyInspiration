package com.mehdi.memo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by johndoe on 10/12/17.
 */

public class DismissService extends IntentService {

    public DismissService() {
        super("DismissService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        int notifId=intent.getIntExtra(getString(R.string.notify_id), 0);
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
