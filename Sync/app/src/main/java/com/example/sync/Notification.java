package com.example.sync;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing notifications.
 */
public class Notification {
    private static String TAG = "Notification";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<String> eventQuery;
    private String eventID;
    private String title;   // event name
    private String message;
    private CollectionReference collection = db.collection("Notifications");
    private DocumentReference document = collection.document();

    /**
     * Constructor for creating a notification.
     *
     * @param id      The ID of the event associated with the notification.
     * @param title   The title of the notification.
     * @param message The message content of the notification.
     */
    public Notification(String id, String title, String message) {
        this.eventID = id;
        this.title = title;
        this.message = message;
    }

    /**
     * Interface for handling notification retrieval callbacks.
     */
    public interface Callback {
        /**
         * Callback method invoked when notifications are successfully retrieved.
         *
         * @param notificationArray An ArrayList containing the retrieved notifications.
         */
        void onSuccess(ArrayList<Notification> notificationArray);
    }

    /**
     * Retrieves notifications for a specific attendee.
     *
     * @param AttendeeId The ID of the attendee to retrieve notifications for.
     * @param callback   The callback listener to handle the retrieved notifications.
     */
    public static void getNotification(String AttendeeId, Callback callback) {
        // Create an array to receive all notifications
        ArrayList<Notification> notificationArray = new ArrayList<Notification>();

        // Find the event
        db.collection("Accounts")
                .whereEqualTo("userId", AttendeeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // get query result
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // extract the eventID
                                Map<String, Object> data = document.getData();
                                eventQuery = (ArrayList<String>) data.get("signupevents");
                            }

                            if (eventQuery.isEmpty()){
                                callback.onSuccess(notificationArray);
                                return;
                            }
                            // Fetch all notifications
                            for (String event: eventQuery) {
                                db.collection("Notifications")
                                        .whereEqualTo("eventID", eventQuery)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                // get query result
                                                if (task.isSuccessful()) {
                                                    // An event may have many notifications
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                                        // extract
                                                        Map<String, Object> data = document.getData();
                                                        String id = (String) data.get("eventID");
                                                        String title = (String) data.get("title");
                                                        String message = (String) data.get("message");

                                                        // create new instance
                                                        Notification notification = new Notification(id, title, message);
                                                        notificationArray.add(notification);
                                                    }

                                                    // call back
                                                    callback.onSuccess(notificationArray);

                                                } else {
                                                    // did not find the event
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Sets the notification in the Firestore database.
     */
    public void setNotification() {
        // put information into the hashmap
        Map<String, Object> notification = new HashMap<>();
        notification.put("eventID", eventID);
        notification.put("title", title);
        notification.put("message", message);

        // push them to the database
        // record the result as a log
        document.set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document uploaded successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error uploading document", e);
                    }
                });

    }

    // Getters
    public String getEventId() { return eventID; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }

}


