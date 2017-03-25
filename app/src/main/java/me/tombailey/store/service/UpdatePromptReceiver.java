package me.tombailey.store.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import me.tombailey.store.R;
import me.tombailey.store.StoreApp;
import me.tombailey.store.UpdatesActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by tomba on 24/03/2017.
 */

public class UpdatePromptReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showUpdatePromptNotification(context);
    }

    protected PendingIntent getUpdatePendingIntent(Context context) {
        Intent updatesActivityIntent = new Intent(context, UpdatesActivity.class);
        return PendingIntent.getActivity(context, 0, updatesActivityIntent, 0);
    }

    protected void showUpdatePromptNotification(Context context) {
        Notification updatePromptNotification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_smartphone_white_24dp)
                .setContentTitle(Resources.getSystem().getString(R.string.app_name))
                .setContentText(Resources.getSystem().getString(R.string.update_prompt_receiver_update_prompt_notification))
                .setAutoCancel(true)
                .setContentIntent(getUpdatePendingIntent(context))
                .build();

        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE))
                .notify(StoreApp.UPDATE_PROMPT_NOTIFICATION_ID, updatePromptNotification);
    }

}
