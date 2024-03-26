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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttendeeDashboard extends AppCompatActivity {
    private ArrayList<Event> events;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);

        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start HomeActivity when the button is clicked
                Intent intent = new Intent(AttendeeDashboard.this, MainActivity.class);
                // Consider clearing the task or setting flags if needed, to prevent back stack accumulation
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Button profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button NotificationButton = findViewById(R.id.messages_button);
        NotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

//        Button setNotificationButton = findViewById(R.id.messages_button);
//        setNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNotification();
//            }
//        });

        Button browseEventButton = findViewById(R.id.event_button);
        browseEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseEvent();
            }
        });
    }
    /**
     * This method set notification and sent it to attendee 1 hour before
     */
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
    /**
     * This method get a few events from firebase and display them
     */
    public void browseEvent(){
        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder descriptions = new StringBuilder();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    descriptions.append(event.getEventName()).append("\n\n");
                }

                // Start EventDetailActivity and pass event details
                Intent intent = new Intent(AttendeeDashboard.this, EventListActivity.class);
                intent.putExtra("eventDetails", descriptions.toString());
                startActivity(intent);
            }
        });
    }
}
