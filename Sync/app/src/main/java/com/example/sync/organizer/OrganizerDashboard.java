package com.example.sync.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.R;

public class OrganizerDashboard extends AppCompatActivity implements CreateEventFrag.CreateEventFragListener {
    private Organizer organizer;

    // find fragments
    View dashboard;
    View createEventContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        // find fragments
        dashboard = findViewById(R.id.dashboard);
        createEventContainer = findViewById(R.id.create_event_container);

        // set visibility
        createEventContainer.setVisibility(View.GONE);

        Button createEvent = dashboard.findViewById(R.id.create_new_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventFrag createEventFrag = CreateEventFrag.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.create_event_container, createEventFrag);
                transaction.commit();

                dashboard.setVisibility(View.GONE);
                createEventContainer.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void notifyShutDown(CreateEventFrag frag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(frag);
        dashboard.setVisibility(View.VISIBLE);
        createEventContainer.setVisibility(View.GONE);
    }
}
