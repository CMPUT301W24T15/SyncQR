package com.example.sync.organizer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.Event;
import com.example.sync.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewEventsFrag extends Fragment{

    private FragListener listener;
    private Toolbar toolbar;
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventListAdapter eventListAdapter;
    private Boolean deleteEvent = false;
    private SearchView search; // has not been implemented
    private ViewEventsFrag self = this;


    static ViewEventsFrag newInstance() {
        // create the fragment instance
        ViewEventsFrag fragment = new ViewEventsFrag();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragListener) {
            listener = (FragListener) context;
        } else {
            throw new RuntimeException(context + " must implement FragListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_events, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        eventList = view.findViewById(R.id.eventList);
    }

    @Override
    public void onStart() {
        super.onStart();

        // event mock
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(2024, Calendar.MARCH, 8);
        Event event = new Event("halfcheck", eventDate.getTime(), "Edmonton", "yiqing liu","its time to finish", "", 0);
        Event event2 = new Event("finalcheck", eventDate.getTime(), "Edmonton", "yiqing liu","its time to finish", "", 0);

        // list set up
        dataList = new ArrayList<Event>();
        dataList.add(event);
        dataList.add(event2);
        eventListAdapter = new EventListAdapter(requireContext(), dataList);
        eventList.setAdapter(eventListAdapter);
        System.out.println("2");

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
                        dataList.remove(event);
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

    @Override
    public void onResume() {
        super.onResume();
        // Reload data source if needed
        // Update adapter and notify data set changed
        //dataList.remove(1);
        System.out.println("1");
        eventListAdapter.notifyDataSetChanged();
    }


}