package com.example.sync.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.R;

public class OrganizerDashboard extends AppCompatActivity implements FragListener {
    private Organizer organizer;

    // find fragments
    View dashboard;
    View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        // find fragments
        dashboard = findViewById(R.id.dashboard);
        fragmentContainer = findViewById(R.id.fragment_container);

        // set visibility
        fragmentContainer.setVisibility(View.GONE);

        Button createEvent = dashboard.findViewById(R.id.create_new_event);
        Button viewEvent = dashboard.findViewById(R.id.view_my_events);

        // click listeners
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventFrag createEventFrag = CreateEventFrag.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, createEventFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                dashboard.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }
        });

        viewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewEventsFrag viewEventsFrag = ViewEventsFrag.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, viewEventsFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                dashboard.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void notifyShutDown(Fragment frag) {
        getSupportFragmentManager().beginTransaction().remove(frag).commit();
        dashboard.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
    }
}
