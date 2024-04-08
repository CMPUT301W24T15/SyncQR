package com.example.sync;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sync.organizer.EventListAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity to display a list of events that the user can sign up for.
 * This activity shows only the events corresponding to the event IDs passed to it.
 */
public class CheckinEventListActivity extends EventListActivity {
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
            Log.d("CheckinEventListActivity", "User ID: " + userID);
            // Fetch sign-up events for the user
            fetchUserSignUpEvents(userID);
        } else {
            Log.d("CheckinEventListActivity", "No User ID found");
        }

        // Set up Gesture Detection
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                            if (diffX < 0) {
                                // Left Swipe, Trigger Delete Confirmation
                                int position = eventList.pointToPosition((int) e1.getX(), (int) e1.getY());
                                showDeleteConfirmationDialog(position);
                                result = true;
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });

        // Set up Touch Listener to detect swipe gestures
        eventList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true; // Consume the touch event
            }
        });
    }

    /**
     * Fetches the check-in events for a specific user by their ID and updates a data list.
     *
     * <p>
     * This method queries the Firestore database for the "checkinevents" field of a specific user
     * in the "Accounts" collection. It then fetches each event using its ID from the "Events" collection.
     * Each successfully fetched event is added to a data list and notifies an associated list adapter
     * to update the UI. If an event does not exist or cannot be fetched, an error message is logged.
     * If the user has no check-in events or does not exist, a log message is also produced.
     * </p>
     *
     * @param userID The unique identifier for the user whose check-in events are being fetched.
     *               This ID is used to locate the user document in the "Accounts" collection.
     */
    private void fetchUserSignUpEvents(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> checkinEventIDs = (ArrayList<String>) documentSnapshot.get("checkinevents");
                        if (checkinEventIDs != null && !checkinEventIDs.isEmpty()) {
                            for (String eventID : checkinEventIDs) {
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
                                                Log.d("CheckinEventListActivity", "Event with ID " + eventID + " does not exist");
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("CheckinEventListActivity", "Error fetching event with ID " + eventID, e));
                            }
                        } else {
                            Log.d("CheckinEventListActivity", "No sign-up events found for user with ID " + userID);
                        }
                    } else {
                        Log.d("CheckinEventListActivity", "User with ID " + userID + " does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("CheckinEventListActivity", "Error fetching user with ID " + userID, e));
    }

    /**
     * Displays a confirmation dialog to delete an event.
     *
     * @param position The position of the event in the list to be deleted.
     */
    private void showDeleteConfirmationDialog(final int position) {
        if (position == ListView.INVALID_POSITION) {
            return; // If touch is detected outside of a valid list item, ignore it.
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Deleting the event locally
                    Event event = dataList.get(position);
                    String eventID = event.getEventId();
                    dataList.remove(position);
                    eventListAdapter.notifyDataSetChanged();

                    // Removing the event from Firestore
                    String userID = getIntent().getStringExtra("userID");
                    DocumentReference userRef = FirebaseFirestore.getInstance().collection("Accounts").document(userID);
                    userRef.update("checkinevents", FieldValue.arrayRemove(eventID))
                            .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event removed from checkinevents array"))
                            .addOnFailureListener(e -> Log.e("Delete Event", "Error removing event from checkinevents array", e));
                    Checkin.quitCheckin(eventID,userID);
                })
                .setNegativeButton("No", null)
                .show();
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
                Intent intent = new Intent(CheckinEventListActivity.this, EventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                String eventID = event.getEventId();
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });
    }
}
