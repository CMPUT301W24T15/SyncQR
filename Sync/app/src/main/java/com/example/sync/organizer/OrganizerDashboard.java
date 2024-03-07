package com.example.sync.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.R;

public class OrganizerDashboard extends AppCompatActivity {
    private Organizer organizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        // find fragments
        View dashboard = findViewById(R.id.dashboard);
        View createEventVContainer = findViewById(R.id.create_event_container);

        // set visibility
        createEventVContainer.setVisibility(View.GONE);

        Button createEvent = dashboard.findViewById(R.id.create_new_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventFrag createEventFrag = CreateEventFrag.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.create_event_container, createEventFrag);
                transaction.commit();

                findViewById(R.id.dashboard).setVisibility(View.GONE);
                findViewById(R.id.create_event_container).setVisibility(View.VISIBLE);

            }
        });

    }
}
