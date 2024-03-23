package com.example.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Notification> adapter; // Adjust this line based on your Notification object structure
    private List<Notification> notifications;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);

        listView = view.findViewById(R.id.notificationsListView); // Make sure you have a ListView in your XML with this ID

        notifications = new ArrayList<>();
        // Adjust the adapter initialization to fit your needs. This assumes Notification has a toString() method that returns the display string.
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, notifications);
        listView.setAdapter(adapter);

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


