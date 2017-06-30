package com.mehdi.memo;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
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

        String message=intent.getStringExtra("MESSAGE");
        //Create an alert dialog builder
        AlertDialog.Builder builder=new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
        builder.setIcon(R.drawable.alert);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setMessage(message);

        //Create the alert dialog
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}
