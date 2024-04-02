package com.example.sync.Close;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sync.Open.Event;
import com.example.sync.R;
import com.example.sync.organizer.EventListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity to display a list of events that the user can sign up for.
 * This activity shows only the events corresponding to the event IDs passed to it.
 */
public class SignUpEventListActivity extends EventListActivity {
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, calling setAdapter on a ListView, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_event);

        Log.d("SignUpEventListActivity", "Activity started.");

        // Initialize the dataList and eventList
        dataList = new ArrayList<>();
        eventList = findViewById(R.id.sign_up_event_list);
        eventListAdapter = new EventListAdapter(this, dataList);
        eventList.setAdapter(eventListAdapter);

        // Retrieve and process the userID passed to this activity
        String userID = getIntent().getStringExtra("userID");
        // Check if the userID is not null and log its content
        if (userID != null) {
            Log.d("SignUpEventListActivity", "User ID: " + userID);
            // Fetch sign-up events for the user
            fetchUserSignUpEvents(userID);
        } else {
            Log.d("SignUpEventListActivity", "No User ID found");
        }

        setupItemClickListener();
    }

    private void fetchUserSignUpEvents(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> signUpEventIDs = (ArrayList<String>) documentSnapshot.get("signupevents");
                        if (signUpEventIDs != null && !signUpEventIDs.isEmpty()) {
                            for (String eventID : signUpEventIDs) {
                                db.collection("Events").document(eventID)
                                        .get()
                                        .addOnSuccessListener(eventDocument -> {
                                            if (eventDocument.exists()) {
                                                Event event = eventDocument.toObject(Event.class);
                                                if (event != null) {
                                                    dataList.add(event);
                                                    eventListAdapter.notifyDataSetChanged();
                                                }
                                            } else {
                                                Log.d("SignUpEventListActivity", "Event with ID " + eventID + " does not exist");
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("SignUpEventListActivity", "Error fetching event with ID " + eventID, e));
                            }
                        } else {
                            Log.d("SignUpEventListActivity", "No sign-up events found for user with ID " + userID);
                        }
                    } else {
                        Log.d("SignUpEventListActivity", "User with ID " + userID + " does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("SignUpEventListActivity", "Error fetching user with ID " + userID, e));
    }


    /**
     * Sets up the item click listener for the event list.
     * When an event is clicked, this method navigates to the EventDetailsActivity,
     * passing the event ID of the clicked event.
     */
    private void setupItemClickListener() {
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = dataList.get(position);
                Intent intent = new Intent(SignUpEventListActivity.this, EventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                String eventID = event.getEventId();
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });
    }
}


