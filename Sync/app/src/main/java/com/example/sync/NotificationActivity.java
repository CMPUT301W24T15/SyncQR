package com.example.sync;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private List<String> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_fragment);

        ListView listView = findViewById(R.id.notificationsListView);

        // Initialize the adapter with a simple list item layout and the notifications list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        listView.setAdapter(adapter);

        // Fetch notifications from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> newNotifications = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Concatenate the title and message for each notification
                            String title = doc.getString("title");
                            String message = doc.getString("message");
                            newNotifications.add(title + ": " + message);
                        }

                        // Update the UI
                        notificationsList.clear();
                        notificationsList.addAll(newNotifications);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}


