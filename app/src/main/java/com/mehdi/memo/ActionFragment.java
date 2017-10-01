package com.mehdi.memo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by johndoe on 10/1/17.
 */

public class ActionFragment extends Fragment {
    public ActionFragment() {
        super();
    }
    public ActionFragment(int notif_id) {
        super();
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notif_id);

    }

}
