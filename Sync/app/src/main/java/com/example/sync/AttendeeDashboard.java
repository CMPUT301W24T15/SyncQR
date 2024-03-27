package com.example.sync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttendeeDashboard extends AppCompatActivity implements LocationPermissionDialog.LocationPermissionDialogListener {
    private ArrayList<Event> signUpEvents;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee);
        Button locationPermissionButton = findViewById(R.id.location_permission_button);
        locationPermissionButton.setOnClickListener(v -> new LocationPermissionDialog().show(getSupportFragmentManager(), "LocationPermissionDialog"));

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
//    /**
//     * This method set notification and sent it to attendee 1 hour before
//     */
//    public void setNotification(){
//        // Create the NotificationChannel
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("event_channel_id",
//                    "Event Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//        // Schedule the notification
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent notificationIntent = new Intent(this, MyNotificationReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Set the time for the notification (1 hour before the event)
//        long eventTimeInMillis = events.get(0).getEventDate().getTime(); // calculate the event time in milliseconds
//        long notificationTime = eventTimeInMillis - 3600000; // 1 hour before the event
//        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
//    }
    /**
     * This method get a few events from firebase and display them
     */
    private void browseEvent(){
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
    /**
     * Fetches events that a user has signed up for.
     * @param userID The ID of the user whose signed-up events are to be fetched.
     * @param callback The callback to handle the fetched events.
     */
    public static void getSignUpEvents(String userID, EventCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Event> signedUpEvents = new ArrayList<>();

        db.collection("events")
                // Query for events where the attendees map has the userID as a key
                .whereEqualTo("attendees." + userID, true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            signedUpEvents.add(event);
                        }
                        callback.onCallback(signedUpEvents);
                    } else {
                        // Handle the error. For simplicity, we're just calling the callback with an empty list.
                        callback.onCallback(signedUpEvents);
                    }
                });

    }
    private static final int YOUR_PERMISSION_REQUEST_CODE = 123;

    // Implement the interface method
    @Override
    public void onRequestLocationPermission() {
        // Check for permission or request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, YOUR_PERMISSION_REQUEST_CODE);
        }
    }
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Explain to the user why the permission is needed
            // Then request the permission
            ActivityCompat.requestPermissions(AttendeeDashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                // You can now use the location services
            } else {
                // Permission denied
                // Disable the functionality that depends on this permission.
            }
        }
    }

}
