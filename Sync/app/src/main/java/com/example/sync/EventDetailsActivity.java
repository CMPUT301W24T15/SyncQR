package com.example.sync;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sync.organizer.FragListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Activity that displays the details of an event.
 */
public class EventDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    FragListener listener;

    ImageView poster; // has not been implemented. implement when connect to database
    TextView name;
    TextView location;
    TextView date;
    //    TextView organizer;
    TextView description;
    TextView limit;

    String TAG = "EventDetailsActivity";

    /**
     * Static method to launch the EventDetailsActivity.
     * This method creates an intent, puts the event ID as an extra, and starts the activity.
     *
     * @param context The Context from which the activity is launched.
     * @param eventId The ID of the event to display.
     */
    public static void startWithEventId(@NonNull Context context, @NonNull String eventId) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra("eventID", eventId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        name = findViewById(R.id.event_name);
        location = findViewById(R.id.event_location);
        date = findViewById(R.id.event_date_time);
        description = findViewById(R.id.event_description);
        limit = findViewById(R.id.event_attendee_limit);
        poster = findViewById(R.id.event_poster);

        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * Sets up the "Sign Up" button with a click listener that calls {@link #incrementSignUpCounter(String)}
         * to increment the sign-up counter for the event in Firebase.
         */
        Button signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assume eventId is the ID of the event being viewed
                String eventId = getIntent().getStringExtra("eventID");
                String userID = getIntent().getStringExtra("userID");
                // Update the user's document in Firestore
                updateUserEventsInFirestore(userID, eventId);
                Checkin.signUpForUser(eventId, userID);
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
                            limit.setText((attendee_limit));

                            if (!Objects.equals(document.getData().get("poster"),"")){
                                Glide.with(EventDetailsActivity.this)
                                        .load(document.getData().get("poster"))
                                        .into(poster);
                            }

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

    /**
     * Updates the Firestore document corresponding to the user's account to add the eventId
     * to the "event" field, which is an ArrayList<String> containing the IDs of events the user
     * has signed up for.
     *
     * @param userIdValue  The ID of the user whose account is being updated.
     * @param eventId The ID of the event to add to the user's list of signed-up events.
     */
    private void updateUserEventsInFirestore(@NonNull String userIdValue, @NonNull String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query for the user document with the matching userId
        db.collection("Accounts").whereEqualTo("userID", userIdValue).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                // Found the user document, now update it
                                DocumentReference userRef = document.getReference();
                                userRef.update("signupevents", FieldValue.arrayUnion(eventId))
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's signed-up events successfully."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding event to user's signed-up events.", e));
                            } else {
                                Log.d(TAG, "No such user document with given userId");
                            }
                        }
                    } else {
                        Log.d(TAG, "Error finding user document: ", task.getException());
                    }
                });
    }
}
