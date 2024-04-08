package com.example.sync.organizer;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.Event;
import com.example.sync.Notification;
import com.example.sync.R;
import java.util.ArrayList;


/**
 * Fragment for viewing events created by the organizer.
 */
public class ViewEventsFrag extends Fragment{

    private FragListener listener;
    private Toolbar toolbar;
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;
    private Boolean deleteEvent = false;
    private SearchView search; // has not been implemented
    private ViewEventsFrag self = this;

    /**
     * Creates a new instance of ViewEventsFrag.
     * @param userId The ID of the user.
     * @return ViewEventsFrag
     */
    static ViewEventsFrag newInstance(String userId) {
        // create the fragment instance
        ViewEventsFrag fragment = new ViewEventsFrag();

        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Called when a fragment is first attached to its context.
     * Attach a listener
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragListener) {
            listener = (FragListener) context;
        } else {
            throw new RuntimeException(context + " must implement FragListener");
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_events, container, false);
        return view;
    }

    /**
     * Link the relevant views
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        eventList = view.findViewById(R.id.eventList);
    }

    /**
     * Called when the Fragment is visible to the user.
     * Obtain all the events from database
     * Set onclick listener so when user click, it will jump to the right fragment
     */
    @Override
    public void onStart() {
        super.onStart();

        // acquire user id
        Bundle args = getArguments();
        String userId = args.getString("userId");


        // list set up
        dataList = new ArrayList<Event>();
        Event.getCreatedEventIdList(userId, new Event.Callback() {
            @Override
            public void onSuccessReturnId(ArrayList<String> idList) {

                // idList may be empty
                // find all events that have been created by the user
                if (!idList.isEmpty()) {
                    for (String eventId: idList) {
                        Event.getEventFromDatabase(eventId, new Event.Callback() {
                            @Override
                            public void onSuccess(Event event) {
                                dataList.add(event);

                                // set an adapter when all events have been loaded
                                if (idList.size() == dataList.size()){
                                    EventListAdapter adapter = new EventListAdapter(requireContext(), dataList);
                                    eventList.setAdapter(adapter);
                                }
                            }
                        });
                    }
                }
            }
        });


        // click list item
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // new fragment
                EventDetailFrag eventDetailFrag = EventDetailFrag.newInstance(dataList.get(position));
                eventDetailFrag.setListener(new EventDetailFrag.EventDetailListener() {
                    @Override
                    public void notifyParent(Event event) {
                        // If the event is deleted, then its relevant fragments will be destroyed.
                        FragmentManager manager = requireActivity().getSupportFragmentManager();
                        manager.popBackStack("event_detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        // Next, delete it from the datalist and the database
                        dataList.remove(event);
                        event.deleteEvent(event.getEventId());

                        // Finally, make an announcement for both organizer and the attendees
                        Notification notification = new Notification(event.getEventId(), event.getEventName(), "This event has been deleted by the organizer / administrator.");
                        notification.setNotification();
                        Toast.makeText(getContext(), "Event has been successfully deleted!", Toast.LENGTH_LONG).show();
                    }
                });

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, eventDetailFrag);
                transaction.addToBackStack("event_detail");
                transaction.commit();

            }

        });

        // back to dashboard
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.notifyShutDown(self);
            }
        });
    }


}