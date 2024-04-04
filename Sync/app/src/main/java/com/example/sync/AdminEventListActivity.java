package com.example.sync;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.organizer.EventListAdapter;

import java.util.ArrayList;

/**
 * Activity for displaying a list of events for administrators.
 * This activity fetches event data from Firestore and displays it in a ListView using an EventListAdapter.
 * Users can swipe left on an event to delete it.
 */
public class AdminEventListActivity extends AppCompatActivity {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Initialize dataList, eventList, and eventListAdapter
        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        eventListAdapter = new EventListAdapter(this, dataList);
        eventList.setAdapter(eventListAdapter);

        // Initialize gesture detector to detect swipe gestures
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        // Fetch events from Firestore and update the UI
        Event.getAllEventFromDatabase(new Event.Callback() {
            @Override
            public void onSuccess(ArrayList<Event> eventArrayList) {
                dataList.addAll(eventArrayList);
                eventListAdapter.notifyDataSetChanged();
            }
        });

        // Set an item click listener on the ListView to handle clicks on individual events
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = dataList.get(position);
                Intent intent = new Intent(AdminEventListActivity.this, AdminEventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                String eventID = event.getEventId();
                intent.putExtra("eventID", eventID);
                startActivity(intent);
            }
        });

        // Set onTouchListener to detect swipe gestures
        eventList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * Custom gesture listener to handle swipe gestures.
     * This listener detects left swipe gestures on the ListView items and triggers the deletion of the corresponding event.
     */
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right
                        return true;
                    } else {
                        // Swipe left
                        int position = eventList.pointToPosition((int) e1.getX(), (int) e1.getY());
                        Event event = dataList.get(position);
                        deleteEvent(position, event);
                        return true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Displays a confirmation dialog to delete the selected event.
     * If the user confirms the deletion, the event is removed from the database and the UI.
     *
     * @param position      The position of the event in the list.
     * @param eventToDelete The event to delete.
     */
    private void deleteEvent(int position, Event eventToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event");
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove event from Firestore
                Event event = dataList.get(position);
                String eventID = event.getEventId();
                Event.deleteEvent(eventID);

                // Remove event from the list
                dataList.remove(position);
                eventListAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
