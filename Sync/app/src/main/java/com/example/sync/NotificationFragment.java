package com.example.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications; // This should be fetched, here it's just a placeholder

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);

        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy data for demonstration
        notifications = new ArrayList<>();
        notifications.add(new Notification("1", "Welcome", "Welcome to our event!"));
        // Add more announcements as needed

        adapter = new NotificationAdapter(getContext(), notifications);
        recyclerView.setAdapter(adapter);

        Button clearButton = view.findViewById(R.id.clearNotificationsButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear announcements list and notify adapter
                notifications.clear();
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}

