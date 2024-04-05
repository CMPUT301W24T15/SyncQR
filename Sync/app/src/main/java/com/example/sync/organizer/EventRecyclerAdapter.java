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

public class EventRecyclerAdapter extends RecyclerView.Adapter {

    private ArrayList<String> nameList;
    private ArrayList<Integer> countList;

    public EventRecyclerAdapter(ArrayList<String> nameList, ArrayList<Integer>countList) {
        this.nameList = nameList;
        this.countList = countList;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.milestone, parent, false);
        return new EventViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView count;
        ProgressBar progressBar;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
