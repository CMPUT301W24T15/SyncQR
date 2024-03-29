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

public class SignUpEventListActivity extends EventListActivity{
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_event);

        Log.d("kevinTag", "Created EventListActivity Instance");

        dataList = new ArrayList<>();
        eventList = findViewById(R.id.sign_up_event_list);
        eventListAdapter = new EventListAdapter(this, dataList);
        eventList.setAdapter(eventListAdapter);
        Event.getAllEventFromDatabase(new Event.Callback() {
            @Override
            public void onSuccess(ArrayList<Event> eventArrayList) {
                dataList.addAll(eventArrayList);
                eventListAdapter.notifyDataSetChanged();
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = dataList.get(position);
                Intent intent = new Intent(SignUpEventListActivity.this, EventDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                String eventID = event.getEventId();
                intent.putExtra("eventID", ID.toString());
                startActivity(intent);
            }
        });
    }
}

