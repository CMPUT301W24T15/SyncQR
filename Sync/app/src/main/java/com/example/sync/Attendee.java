package com.example.sync;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class Attendee extends User {
    private ArrayList<Event> events;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee); // Replace your_layout_file with the actual file name

//        Button homeButton = findViewById(R.id.home_Button);
//        homeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Start HomeActivity when the button is clicked
//                Intent intent = new Intent(CurrentActivity.this, Attendee.class);
//                // Consider clearing the task or setting flags if needed, to prevent back stack accumulation
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//            }
//        });

        Button setNotificationButton = findViewById(R.id.set_Notification_Button);
        setNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotification();
            }
        });

        Button browseEventButton = findViewById(R.id.browse_event_button);
        browseEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseEvent();
            }
        });
    }

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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the time for the notification (1 hour before the event)
        long eventTimeInMillis = events.get(0).getEventDate().getTime(); // calculate the event time in milliseconds
        long notificationTime = eventTimeInMillis - 3600000; // 1 hour before the event
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }
    public void browseEvent(){
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder descriptions = new StringBuilder();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    descriptions.append(event.getEventDescription()).append("\n\n");
                }
                updateUI(descriptions.toString()); // Implement this method to update your UI
            }
        });
    }

    private void updateUI(String descriptions) {
        // Assuming you have a TextView with the id 'textViewEventDescriptions'
        TextView textView = findViewById(R.id.textViewEventDescriptions);
        textView.setText(descriptions);
    }

    @Override
    public void onConfirmPressed(Profile newProfile) {
        ;
    }
}
