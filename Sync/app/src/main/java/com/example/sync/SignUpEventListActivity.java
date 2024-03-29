package com.example.sync;

import static android.os.Build.ID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sync.organizer.EventListAdapter;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

        // Retrieve and process the event IDs passed to this activity
        ArrayList<String> eventIDs = getIntent().getStringArrayListExtra("eventIDs");
        if (eventIDs != null && !eventIDs.isEmpty()) {
            fetchEventsByIds(eventIDs);
        }

        setupItemClickListener();
    }

    /**
     * Fetches events by their IDs and updates the list.
     *
     * @param eventIDs A list of event IDs to fetch from the database.
     */
    private void fetchEventsByIds(ArrayList<String> eventIDs) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String eventId : eventIDs) {
            db.collection("events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    dataList.add(event);
                    eventListAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> Log.e("SignUpEventListActivity", "Error fetching event", e));
        }
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


