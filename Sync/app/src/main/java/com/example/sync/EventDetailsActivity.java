package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sync.organizer.FragListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

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
                // Update the user's document in Firestore
                updateUserEventsInFirestore("1718521", eventId);
                addUserToSignUpList(eventId, "1718521");
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventID = extras.getString("eventID");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("events").document(eventID);

//            final Map<String, Object> data;
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

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                            date.setText(sdf.format(document.getData().get("eventDate")));

                            String event_description = "Description: " + document.getData().get("eventDescription");
                            description.setText(event_description);

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
     * Adds the user's ID to the "sign-up" list in the "check-ins" system for a specific event.
     *
     * @param eventId The ID of the event for which to add the user's ID to the "sign-up" list.
     * @param userId  The ID of the user who signed up for the event.
     */
    private void addUserToSignUpList(@NonNull String eventId, @NonNull String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Assuming "check-ins" is a document related to the eventID in some way
        DocumentReference checkInRef = db.collection("Check-in System").document(eventId);

        // Use update method to add the userId to the "sign-up" list
        checkInRef.update("sign-up", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to sign-up list successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to sign-up list.", e));
    }
    /**
     * Updates the Firestore document corresponding to the user's account to add the eventId
     * to the "event" field, which is an ArrayList<String> containing the IDs of events the user
     * has signed up for.
     *
     * @param userId  The ID of the user whose account is being updated.
     * @param eventId The ID of the event to add to the user's list of signed-up events.
     */
    private void updateUserEventsInFirestore(@NonNull String userId, @NonNull String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        // Update the "events" field in the user's document with the new event ID
        userRef.update("event", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's signed-up events successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event to user's signed-up events.", e));
    }
}