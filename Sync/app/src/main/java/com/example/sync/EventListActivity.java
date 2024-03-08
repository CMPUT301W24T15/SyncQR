package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.organizer.EventListAdapter;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Log.d("kevinTag", "Created EventListActivity Instance");

        Button homeButton = findViewById(R.id.home_button);
        Button profileButton = findViewById(R.id.profile_button);
        Button eventButton = findViewById(R.id.event_button);
        Button messagesButton = findViewById(R.id.messages_button);

        dataList = new ArrayList<Event>();

        eventList = findViewById(R.id.event_list);
        eventListAdapter = new EventListAdapter(this, dataList);
        eventList.setAdapter(eventListAdapter);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, MainActivity.class); // Assuming you have a ProfileActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, Profile.class); // Assuming you have a ProfileActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
            }
        });

        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // not implemented yet
//                Intent intent = new Intent(EventListActivity.this, MessageActivity.class);
//                startActivity(intent);
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = dataList.get(position);
                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                Integer ID = event.getEventId();
                intent.putExtra("eventID", ID);
                startActivity(intent);
            }
        });

    }
}
