package com.example.sync.organizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.sync.Event;
import com.example.sync.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Display event details to the organizer (mimics the interface that the user will look through)
 * This is a subclass of Fragment
 * @see Fragment
 */
public class DisplayFrag extends Fragment {

    /**
     * The view for event poster
     */
    ImageView poster;
    /**
     * The view for name of the event
     */
    TextView name;
    /**
     * The view for date of the event
     */
    TextView date;
    /**
     * The view for location of the event
     */
    TextView location;
    /**
     * The view for organizer name of the event
     */
    TextView organizer;
    /**
     * The view for description of the event
     */
    TextView description;

    /**
     * Create a new instance of the dialog with an argument
     * Call its constructor within the static method
     * @param event The event instance that needs to be displayed
     * @return DisplayFrag
     */
    static DisplayFrag newInstance(Event event) {
        // create the fragment instance
        DisplayFrag fragment = new DisplayFrag();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Inflate a layout
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_organizer, container, false);
        return view;
    }

    /**
     * Link to all relevant views
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.name);
        date = view.findViewById(R.id.date);
        location = view.findViewById(R.id.location);
        organizer = view.findViewById(R.id.organizer);
        description = view.findViewById(R.id.description);
        poster = view.findViewById(R.id.poster);
    }

    /**
     * Determine the procedure after the fragment has been created
     * Fill out all textviews and get poster from the database storage
     */
    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            Event event = (Event) args.getSerializable("event");
            name.setText(event.getEventName());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            date.setText(sdf.format(event.getEventDate().toDate()));

            String event_location = "Location:  " + event.getEventLocation();
            location.setText(event_location);

            String event_organizer = "Organizer:  " + event.getOrganizerName();
            organizer.setText(event_organizer);

            description.setText(event.getEventDescription());

            if (!Objects.equals(event.getPoster(), "")) {
                Glide.with(requireContext())
                        .load(event.getPoster())
                        .into(poster);
            }
        }
    }
}
