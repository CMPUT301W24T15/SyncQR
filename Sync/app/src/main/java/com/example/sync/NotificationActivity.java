package com.example.sync;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display notifications fetched from Firestore.
 */
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = getIntent().getStringExtra("userID");
        Log.d("UserID", "User ID: " + userID);

        // Fetch signupevents for the specific userID
        db.collection("Accounts")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> eventIds = (List<String>) document.get("signupevents");

                                if (eventIds != null && !eventIds.isEmpty()) {
                                    for (String eventId : eventIds) {
                                        // Fetch notifications for the current event ID
                                        db.collection("Notifications")
                                                .whereEqualTo("eventID", eventId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            List<String> newNotifications = new ArrayList<>();
                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                // Concatenate the title and message for each notification
                                                                String title = doc.getString("title");
                                                                String message = doc.getString("message");
                                                                newNotifications.add(title + ": " + message);
                                                            }

                                                            // Update the UI
                                                            notificationsList.addAll(newNotifications);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d(TAG, "No sign-up events found for the user.");
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }
}
