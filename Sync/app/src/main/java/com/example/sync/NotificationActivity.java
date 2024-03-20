package com.example.sync;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private NotificationAdapter adapter;
    private List<Notification> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_fragment);

        RecyclerView recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(this, notificationsList);
        recyclerView.setAdapter(adapter);

        // Fetch notifications from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<Notification> newNotifications = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Assuming document fields are named "title" and "message"
                            String title = doc.getString("title");
                            String message = doc.getString("message");
                            // You might want to include an id or timestamp
                            Notification notification = new Notification(doc.getId(), title, message);
                            newNotifications.add(notification);
                        }

                        // Update the UI
                        notificationsList.clear();
                        notificationsList.addAll(newNotifications);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

