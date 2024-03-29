package com.festinare.discount.tools.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.festinare.discount.ui.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {

    private static final int NOTIFICATION_ALERT_ID = 1;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//
//        String messageType = gcm.getMessageType(intent);
//        Bundle extras = intent.getExtras();
//
//        if (!extras.isEmpty())
//        {
//            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
//            {
//                showNotification(extras.getString("msg"));
//            }
//        }
//
//        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String msg)
    {
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(android.R.drawable.stat_sys_warning)
//                        .setContentTitle("New Discount")
//                        .setContentText(msg);
//
//        //TODO decide where to go: MainActivity or discount info activity
//        Intent notIntent =  new Intent(this, MainActivity.class);
//        PendingIntent contIntent = PendingIntent.getActivity(
//                this, 0, notIntent, 0);
//
//        mBuilder.setContentIntent(contIntent);
//
//        mNotificationManager.notify(NOTIFICATION_ALERT_ID, mBuilder.build());
    }
}
