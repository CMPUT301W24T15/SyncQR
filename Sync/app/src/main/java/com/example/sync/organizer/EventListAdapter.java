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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Custom ArrayAdapter for displaying a list of events in a ListView.
 * This adapter is responsible for creating views for each item in the list,
 * using the layout defined in event_list_item.xml.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    /**
     * Constructs a new EventListAdapter with the given context and list of events.
     * @param context The context in which the adapter is being used.
     * @param events The list of events to be displayed (Source list).
     */
    public EventListAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }


    /**
     * Gets a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return View
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // parent layout setting up
        View view;
        // recycle old view or create a new view
        // dynamically link to the outer layout
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(com.example.sync.R.layout.event_list_item,
                    parent, false);
        } else {
            view = convertView;
        }


        // link to the views
        TextView eventName  = view.findViewById(com.example.sync.R.id.event_name);
        TextView eventDate = view.findViewById(com.example.sync.R.id.event_date);

        // set text
        Event event = getItem(position);
        eventName.setText(event.getEventName());

        SimpleDateFormat sfd = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String date = sfd.format(event.getEventDate().toDate());
        eventDate.setText(date);

        return view;
    }
}
