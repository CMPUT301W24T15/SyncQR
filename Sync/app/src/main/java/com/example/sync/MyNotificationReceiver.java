package com.example.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("event_name");
        showNotification(context, eventName);
    }

    private void showNotification(Context context, String eventName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "event_channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Upcoming Event")
                .setContentText("Event: " + eventName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
    }
}
