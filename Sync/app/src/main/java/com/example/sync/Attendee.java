package com.example.sync;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;


import java.util.ArrayList;

public class Attendee extends User {
    private ArrayList<Event> events;
    public void scanQRCode(){
        new QRCodeScanActivity();
    }
    private void editProfile(Profile editProfile){
        Button editProfileButton = findViewById(R.id.edit_File_Button);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EditProfileFragment((Profile)profileList.getItemAtPosition(i)).show(getSupportFragmentManager(),"Add/Edit_City");

            }
        });
    }
    Button setNotificationButton = findViewById(R.id.set_Notification_Button);
    setNotificationButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setNotification();
        }
    });
    public void setNotification(){
        // Create the NotificationChannel
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("event_channel_id",
                    "Event Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // Schedule the notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, MyNotificationReceiver.class);
        notificationIntent.putExtra("event_name", eventName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the time for the notification (1 hour before the event)
        long eventTimeInMillis = ... // calculate the event time in milliseconds
        long notificationTime = eventTimeInMillis - 3600000; // 1 hour before the event
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }
    Button browseEventButton = findViewById(R.id.browse_event_button);
    browseEventButton.set.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            browseEvent();
        }
    });
    public void browseEvent(){
        events = getEventInfo();
        for (Event event : events) {
            event.show();
        }
    }

}
