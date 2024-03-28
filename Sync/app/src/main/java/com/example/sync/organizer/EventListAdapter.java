package com.example.sync.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sync.Event;
import com.example.sync.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventListAdapter extends ArrayAdapter<Event> {

    // call super
    public EventListAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    // reconstruct getView() method
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // parent layout setting up
        View view;
        // recycle old view or create a new view
        // dynamically link to the outer layout
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item,
                    parent, false);
        } else {
            view = convertView;
        }


        // link to the views
        TextView eventName  = view.findViewById(R.id.event_name);
        TextView eventDate = view.findViewById(R.id.event_date);

        // set text
        Event event = getItem(position);
        eventName.setText(event.getEventName());
        System.out.println("eventlistadapter");

        SimpleDateFormat sfd = new SimpleDateFormat("yyyy.MM.DD HH:mm");
        String date = sfd.format(event.getEventDate().toDate());
        eventDate.setText(date);

        return view;
    }
}
