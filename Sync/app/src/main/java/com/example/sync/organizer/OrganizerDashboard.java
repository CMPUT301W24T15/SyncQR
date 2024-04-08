package com.example.sync.organizer;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sync.Checkin;
import com.example.sync.Event;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


/**
 * OrganizerDashboard class represents the dashboard for an organizer user.
 * It displays options for the organizer to create new events, view existing events,
 * and provides statistics on the events they manage.
 */
public class OrganizerDashboard extends AppCompatActivity implements FragListener {

    /**
     * The currently logged-in organizer.
     */
    private String organizer = "1718521";
    // find fragments
    /**
     * The root view of the dashboard.
     */
    View dashboard;
    /**
     * The container for fragments within the dashboard.
     */
    View fragmentContainer;
    /**
     * Adapter for the RecyclerView displaying event statistics.
     */
    EventRecyclerAdapter adapter;
    /**
     * RecyclerView to display event statistics.
     */
    RecyclerView recyclerView;
    /**
     * List to store the count of attendees for each event.
     */
    ArrayList<Integer> countList = new ArrayList<>();
    /**
     * List to store the names of events.
     */
    ArrayList<String> nameList = new ArrayList<>();
    /**
     * List to store the IDs of events.
     */
    ArrayList<String> idList = new ArrayList<>();

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sync.R.layout.activity_organizer);

        // find fragments
        dashboard = findViewById(com.example.sync.R.id.dashboard);
        fragmentContainer = findViewById(com.example.sync.R.id.fragment_container);

        // set visibility
        fragmentContainer.setVisibility(View.GONE);

        Button createEvent = dashboard.findViewById(com.example.sync.R.id.create_new_event);
        Button viewEvent = dashboard.findViewById(com.example.sync.R.id.view_my_events);
        recyclerView = dashboard.findViewById(com.example.sync.R.id.recycler_view);

        // initialize list for milestones
        // ****************** change to userId intent ***************
        Event.getCreatedEventIdList(organizer, new Event.Callback() {
            @Override
            public void onSuccessReturnId(ArrayList<String> ids) {
                idList = ids;
                if (!idList.isEmpty()) {

                    // record event id based on the order (sequence) they sent back
                    // resolve thread issue
                    ArrayList<String> order = new ArrayList<>();

                    for (String id : idList) {
                        Checkin.getListFromDatabase(id, new Checkin.Callback() {
                            @Override
                            public void onSuccess(String eventId, String eventName, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                                order.add(eventId);
                                countList.add(current.size());
                                nameList.add(eventName);

                                // check if it reaches important milestone
                                invokeCongrat(eventName, current.size());

                                // add listener for each event
                                int index = order.indexOf(id);
                                Checkin.addListenerToEvent(id, index, countList, new Checkin.Callback() {
                                    @Override
                                    public void onSuccessUpdate(String eventName, int newCount) {
                                        if (adapter!=null) {
                                            countList.set(index, newCount);
                                            adapter.notifyItemChanged(index);

                                            // check if it reaches important milestone
                                            invokeCongrat(eventName, newCount);
                                        }
                                    }
                                });

                                // set adapter after all lists are ready. need a reference so use idList.size()
                                if (nameList.size() == idList.size()) {

                                    // Set RecyclerView adapter
                                    idList = order;  // only keep with the correct order list
                                    recyclerView.setLayoutManager(new LinearLayoutManager(OrganizerDashboard.this));
                                    adapter = new EventRecyclerAdapter(nameList, countList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        });
                    }
                }
            }
        });


        // click listeners
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ****************** change to userId intent ***************
                CreateEventFrag createEventFrag = CreateEventFrag.newInstance(organizer);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(com.example.sync.R.id.fragment_container, createEventFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                dashboard.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }
        });

        viewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ****************** change to userId intent ***************
                ViewEventsFrag viewEventsFrag = ViewEventsFrag.newInstance(organizer);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(com.example.sync.R.id.fragment_container, viewEventsFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                dashboard.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);

            }
        });

    }

    /**
     * Shut down the create event fragment
     * Check if the created list has been updated
     * @param frag The fragment to be shut down.
     */
    @Override
    public void notifyShutDown(Fragment frag) {
        getSupportFragmentManager().beginTransaction().remove(frag).commit();
        dashboard.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        // Add new event to dashboard
        // ****************** change to userId intent ***************
        Event.getCreatedEventIdList(organizer, new Event.Callback() {
            @Override
            public void onSuccessReturnId(ArrayList<String> newList) {
                if (!idList.equals(newList)) {

                    for (String item : newList) {
                        if (!idList.contains(item)) {
                            idList.add(item);
                        }
                    }

                    String newID = idList.get(idList.size() - 1);
                    Checkin.getListFromDatabase(newID, new Checkin.Callback() {
                        @Override
                        public void onSuccess(String eventId, String eventName, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                            nameList.add(eventName);
                            countList.add(current.size());
                            adapter.notifyItemChanged(nameList.size() - 1);

                            // add listener for new event
                            int index = idList.size()-1;
                            Checkin.addListenerToEvent(newID, index, countList, new Checkin.Callback() {
                                @Override
                                public void onSuccessUpdate(String name, int newCount) {
                                    if (adapter!=null) {
                                        countList.set(index, newCount);
                                        adapter.notifyItemChanged(index);

                                        // check if it reaches milestone
                                        invokeCongrat(name, newCount);
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    /**
     * Invokes a congratulations dialog if the event reaches a milestone.
     * @param name The name of the event.
     * @param size The number of attendees for the event.
     */
    public void invokeCongrat(String name, int size) {
        // check if it reaches important milestone
        if (size == 10 || size == 20 || size == 40 || size == 80){
            CongraDialog congraDialog = CongraDialog.newInstance(name, String.valueOf(size));
            congraDialog.show(getSupportFragmentManager(), "congratulations!");
        }
    }
}
