package com.example.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);

        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notifications);
        recyclerView.setAdapter(adapter);

        Button clearButton = view.findViewById(R.id.clearNotificationsButton);
        clearButton.setOnClickListener(v -> clearNotifications());

        // Initialize Firestore and load data
        db = FirebaseFirestore.getInstance();
        loadNotificationsFromFirestore();

        return view;
    }

    private void loadNotificationsFromFirestore() {
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentChange doc : querySnapshot.getDocumentChanges()) {
                                Notification notification = doc.getDocument().toObject(Notification.class);
                                notifications.add(notification);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // Handle failure
                    }
                });
    }

    private void clearNotifications() {
        // Optionally clear them from Firestore too
        notifications.clear();
        adapter.notifyDataSetChanged();
    }
}


