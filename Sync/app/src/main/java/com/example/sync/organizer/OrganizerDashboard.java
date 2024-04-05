package com.example.sync.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sync.Checkin;
import com.example.sync.Event;
import com.example.sync.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class OrganizerDashboard extends AppCompatActivity implements FragListener {
    private Organizer organizer;

    // find fragments
    View dashboard;
    View fragmentContainer;
    EventRecyclerAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Integer> countList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();

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
        recyclerView = dashboard.findViewById(R.id.recycler_view);

        // initialize list for milestones
        Event.getCreatedEventIdList("1718521", new Event.Callback() {
            @Override
            public void onSuccessReturnId(ArrayList<String> ids) {
                idList = ids;

                // if initially the user does not create any events
                if (idList.isEmpty()) {
                    Event.addCreatedEventListener("1718521", new Event.Callback() {
                        @Override
                        public void onSuccessReturnId(ArrayList<String> idList) {
                            addMillstoneListener(idList);
                        }
                    });

                    // if initially the user has created some events
                }else {
                    addMillstoneListener(idList);
                }
            }
        });



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

    private void addMillstoneListener(ArrayList<String> idList){

        // iterate each event id to get their checkin-system (obtain event name and real-time attendance)
        for(String id: idList) {
            Checkin.getListFromDatabase(id, new Checkin.Callback() {
                @Override
                public void onSuccess(String eventName, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                    countList.add(current.size());
                    nameList.add(eventName);

                    if (nameList.size() == idList.size()) {

                        // Set RecyclerView adapter
                        recyclerView.setLayoutManager(new LinearLayoutManager(OrganizerDashboard.this));
                        adapter = new EventRecyclerAdapter(nameList, countList);
                        recyclerView.setAdapter(adapter);

                        // milestone listener
                        for (String id: idList) {
                            int position = idList.indexOf(id);
                            Checkin.addListenerToEvents(id, position, countList, new Checkin.Callback() {
                                @Override
                                public void onSuccessUpdate(ArrayList<Integer> newList) {
                                    countList = newList;
                                    adapter.notifyItemChanged(position);
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
