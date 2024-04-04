package com.example.sync;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sync.organizer.FragListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

/**
 * Activity that displays the details of an event for administrators.
 * Users can view event details such as name, location, date, description, and attendee limit.
 * Administrators can also remove the event using the remove button.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    FragListener listener;

    ImageView poster; // This ImageView has not been implemented yet. It can be implemented when connecting to a database.
    TextView name;
    TextView location;
    TextView date;
    TextView description;
    TextView limit;

    String TAG = "AdminEventDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_event_detail);

        name = findViewById(R.id.event_name);
        location = findViewById(R.id.event_location);
        date = findViewById(R.id.event_date_time);
        description = findViewById(R.id.event_description);
        limit = findViewById(R.id.event_attendee_limit);

        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button removeButton = findViewById(R.id.remove_event);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assume eventId is the ID of the event being viewed
                String eventId = getIntent().getStringExtra("eventID");
                // Delete event from database
                Event.deleteEvent(eventId);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventId = getIntent().getStringExtra("eventID");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("Events").document(eventId);

            eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            String nameString = document.getData().get("eventName").toString();
                            name.setText(nameString);

                            String event_location = "Location:  " + document.getData().get("eventLocation");
                            location.setText(event_location);

                            Timestamp eventTimestamp = document.getTimestamp("eventDate");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                            String event_Time = sdf.format(eventTimestamp.toDate());
                            date.setText(event_Time);

                            String event_description = "Description: " + document.getData().get("eventDescription");
                            description.setText(event_description);

                            String attendee_limit = "Attendee Limit: " + document.getData().get("attendeeNumber").toString();
                            limit.setText(attendee_limit);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}
