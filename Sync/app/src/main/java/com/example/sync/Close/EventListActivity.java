package com.example.sync.Close;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.Open.Event;
import com.example.sync.R;
import com.example.sync.organizer.EventListAdapter;

import java.util.ArrayList;

/**
 * Activity for displaying a list of events.
 * This activity fetches event data from Firestore and displays it in a ListView using an EventListAdapter.
 * Users can click on an event to view its details.
 */
public class EventListActivity extends AppCompatActivity {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;

    /**
     * Called when the activity is starting.
     * This is where most initialization should go: calling setContentView(int) to inflate
     * the activity's UI, using findViewById(int) to programmatically interact with widgets in the UI,
     * calling setAdapter on the ListView, and setting up the Firestore database listener to populate the ListView.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Log.d("kevinTag", "Created EventListActivity Instance");

        // Initialize the dataList, eventList, and eventListAdapter
        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        eventListAdapter = new EventListAdapter(this, dataList);
        eventList.setAdapter(eventListAdapter);

        // Fetch events from Firestore and update the UI
        Event.getAllEventFromDatabase(new Event.Callback() {
            @Override
            public void onSuccess(ArrayList<Event> eventArrayList) {
                // Update dataList and notify the adapter to refresh the ListView
                dataList.addAll(eventArrayList);
                eventListAdapter.notifyDataSetChanged();
            }
        });

        // Set an item click listener on the ListView to handle clicks on individual events
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = dataList.get(position);
                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                // Get and pass eventID
                String eventID = event.getEventId();
                intent.putExtra("eventID", eventID);

                // Get and pass userID
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
}
