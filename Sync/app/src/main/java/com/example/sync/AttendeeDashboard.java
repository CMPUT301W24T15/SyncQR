package com.example.sync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttendeeDashboard extends AppCompatActivity implements LocationPermissionDialog.LocationPermissionDialogListener {
    private ArrayList<String> signUpEventIDs;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_attendee);
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

        Button eventButton = findViewById(R.id.event_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, SignUpEventListActivity.class);
                signUpEventIDs = new ArrayList<>();
                signUpEventIDs.add("1234");
                intent.putStringArrayListExtra("eventIDs", signUpEventIDs);
                startActivity(intent);
            }
        });

        Button browseEventButton = findViewById(R.id.browse_event_button);
        browseEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, EventListActivity.class);
                startActivity(intent);;
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

    private void createNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_notification_channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(0, builder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_notification_channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
