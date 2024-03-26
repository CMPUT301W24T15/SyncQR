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
import androidx.fragment.app.Fragment;

import com.example.sync.Event;
import com.example.sync.R;

import java.text.SimpleDateFormat;

public class DisplayFrag extends Fragment {

    ImageView poster; // has not been implemented. implement when connect to database
    TextView name;
    TextView date;
    TextView location;
    TextView organizer;
    TextView description;
    static DisplayFrag newInstance(Event event) {
        // create the fragment instance
        DisplayFrag fragment = new DisplayFrag();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_organizer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        location = view.findViewById(R.id.location);
        organizer = view.findViewById(R.id.organizer);
        description = view.findViewById(R.id.description);

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

    }
}
