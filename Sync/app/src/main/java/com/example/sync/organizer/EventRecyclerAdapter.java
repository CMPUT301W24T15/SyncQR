package com.example.sync.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sync.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * RecyclerView adapter for displaying a list of events with associated counts and progress bars.
 * Each item in the list corresponds to a milestone, showing the name of the event,
 * the count associated with it, and a progress bar indicating the current check in status.
 */
public class EventRecyclerAdapter extends RecyclerView.Adapter {

    /**
     * The source list of names
     */
    private ArrayList<String> nameList;
    /**
     * The source list of counts
     */
    private ArrayList<Integer> countList;

    /**
     * Constructs a new EventRecyclerAdapter with the given lists of milestone names and counts.
     * @param nameList The list of milestone names.
     * @param countList The list of milestone counts.
     */
    public EventRecyclerAdapter(ArrayList<String> nameList, ArrayList<Integer>countList) {
        this.nameList = nameList;
        this.countList = countList;

    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.milestone, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. max is 80
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventViewHolder eventViewHolder = (EventViewHolder) holder;
        eventViewHolder.name.setText(nameList.get(position));
        eventViewHolder.count.setText(String.valueOf(countList.get(position)));

        int current = countList.get(position);
        int max = 80;
        int progress = (current * 100) / max;
        eventViewHolder.progressBar.setProgress(progress);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return nameList.size();
    }

    /**
     * ViewHolder class to hold references to the views for each item in the RecyclerView.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        /**
         * The name of the event
         */
        TextView name;
        /**
         * The count of the event
         */
        TextView count;
        /**
         * The progressBar shown current progress
         */
        ProgressBar progressBar;

        /**
         * Constructs a new EventViewHolder with the given View.
         * @param itemView The View associated with the ViewHolder.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
