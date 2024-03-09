package com.example.sync.organizer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.Event;
//import com.example.sync.R;

import java.text.SimpleDateFormat;

public class EventDetailFrag extends Fragment {
    Toolbar toolbar;
    FragListener listener;

    ImageView poster; // has not been implemented. implement when connect to database
    TextView name;
    TextView date;
    TextView location;
    TextView organizer;
    TextView description;
    public static EventDetailFrag newInstance(Event event) {
        // create the fragment instance
        EventDetailFrag fragment = new EventDetailFrag();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

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
        View view = inflater.inflate(com.example.sync.R.layout.event_detail_organizer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(com.example.sync.R.id.toolbar);
        name = view.findViewById(com.example.sync.R.id.name);
        date = view.findViewById(com.example.sync.R.id.date);
        location = view.findViewById(com.example.sync.R.id.location);
        organizer = view.findViewById(com.example.sync.R.id.organizer);
        description = view.findViewById(com.example.sync.R.id.description);

    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            Event event = (Event) args.getSerializable("event");
            name.setText(event.getEventName());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            date.setText(sdf.format(event.getEventDate()));

            String event_location = "Location:  " + event.getEventLocation();
            location.setText(event_location);

            String event_organizer = "Organizer:  " + event.getOrganizerName();
            organizer.setText(event_organizer);

            description.setText(event.getEventDescription());
        }

        // back to view my events
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
