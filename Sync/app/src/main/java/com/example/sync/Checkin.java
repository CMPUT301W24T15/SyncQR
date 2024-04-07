package com.example.sync;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Checkin System for checking in to an event and storing related information.
 */

public class Checkin {
    private String eventId;
    private String eventName;
    private String qrcode;
    private ArrayList<String> signup;
    private HashMap<String, Integer> checkinCounts;
    private ArrayList<String> checkinCurrent;
    private ArrayList<GeoPoint> geoPoints;
    private static final CollectionReference collection = FirebaseFirestore.getInstance().collection("Checkin System");
    private static final String TAG = "Checkin System";


    public interface Callback {
        default void onSuccess(String eventName, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup){}
        default void onSuccess(ArrayList<LatLng> locations){}
        default void onSuccess(GeoPoint geoPoint){}
        default void onSuccessUpdate(int newCount){};
        default void onSavedQRCode(String text){};
    }

    /**
     * Construct a new Checkin system instance
     * @param eventId The event which the checkin system attached to
     */
    public Checkin(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.qrcode = "";
        this.signup = new ArrayList<>();
        this.checkinCounts = new HashMap<>();
        this.checkinCurrent = new ArrayList<>();
        this.geoPoints = new ArrayList<>();
    }

    /**
     * Initialize a Checkin System document in Firebase
     * @see CollectionReference
     */
    public void initializeDatabase() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("eventId", eventId);
        data.put("eventName", eventName);
        data.put("signup", signup);
        data.put("qrcode", qrcode);
        data.put("checkinCounts", checkinCounts);
        data.put("checkinCurrent", checkinCurrent);
        data.put("locations", geoPoints);

        collection.document(eventId).set(data);

    }


    /**
     * Adds the user's ID to the "signup" list in the "Checkin" system for a specific event.
     *
     * @param eventId The ID of the event for which to add the user's ID to the "sign-up" list.
     * @param userId  The ID of the user who signed up for the event.
     * @see DocumentReference
     */
    public static void signUpForUser(String eventId, String userId) {
        DocumentReference doc = collection.document(eventId);
        doc.update("signup", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to sign-up list successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to sign-up list.", e));
    }


    /**
     * Keep track and update the number of checkin times regarding the user
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     */
    public static void updateCheckinCounts(String eventId, String userId) {
        DocumentReference doc = collection.document(eventId);

        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                // Obtain the map
                if (document.exists()) {
                    Map<String, Object> counts = (Map<String, Object>) document.getData().get("checkinCounts");

                    // if counts is null, initialize a new HashMap
                    if (counts == null) {
                        counts = new HashMap<>();
                    }

                    // deal with new user
                    long newCount = counts.containsKey(userId) ? ((long) counts.get(userId)) + 1 : 1;
                    counts.put(userId, newCount);

                    // update document
                    doc.update("checkinCounts", counts)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }
        });
    }

    /**
     * Keep track current users who checked in.
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     */
    public static void updateCheckinCurrent(String eventId, String userId) {
        Log.d(TAG, "Updating checkinCurrent for event: " + eventId + " with user: " + userId);
        DocumentReference doc = collection.document(eventId);
        doc.update("checkinCurrent", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to checkinCurrent list successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to checkinCurrent list.", e));
    }

    /**
     * Record all locations from attendee
     * @param eventId The id of the correspondent event
     * @param geoPoint The geo point of the user who checked in
     */
    public static void updateLocation(String eventId, GeoPoint geoPoint){
        DocumentReference doc = collection.document(eventId);
        doc.update("locations", FieldValue.arrayUnion(geoPoint))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to location list successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding user to location list.", e));
    }

    /**
     * When the qr code is scanned, updated the three of the lists at the same time
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     //* @param geoPoint The geoPoint of the correspondent user
     */
    //public static void checkInForUser(String eventId, String userId, GeoPoint geoPoint) {
    public static void checkInForUser(String eventId, String userId) {
        // Query for the user document with the matching userId
        registerCheckinForUser(eventId, userId);

        // Proceed with other updates that do not depend on the location
        updateCheckinCurrent(eventId, userId);
        updateCheckinCounts(eventId, userId);
    }

    /**
     * When the user wants to quit a event, the current checkin list should be updated.
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     */
    public static void quitCheckin(String eventId, String userId) {
        DocumentReference doc = collection.document(eventId);
        doc.update("checkinCurrent", arrayRemove(userId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User deleted from checkinCurrent list successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error removing user from checkinCurrent list.", e));
    }


    /**
     * Obtain three lists (map) from the database
     * @param eventId The id of the correspondent event
     * @param callback A callback returned all three list when data fetch finished
     */
    public static void getListFromDatabase(String eventId, Callback callback) {
        DocumentReference doc = collection.document(eventId);

        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                // Obtain the map
                if (document.exists()) {
                    String eventName = (String) document.getData().get("eventName");
                    HashMap <String, Object> counts = (HashMap<String, Object>) document.getData().get("checkinCounts");
                    ArrayList<String> current = (ArrayList<String>) document.getData().get("checkinCurrent");
                    ArrayList<String> signup = (ArrayList<String>) document.getData().get("signup");

                    callback.onSuccess(eventName, counts, current, signup);
                }
            }
        });
    }

    /**
     * Obtain all users' location information from database
     * @param eventId The id of the correspondent event
     * @param callback A callback returned the list with all location information
     */
    public static void getLocationFromDatabase(String eventId, Callback callback) {
        collection.whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // check if the query is completed
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // record log
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Obtain relevant fields
                                Map<String, Object> data = document.getData();
                                ArrayList<GeoPoint> locations = (ArrayList<GeoPoint>) data.get("locations");
                                ArrayList<LatLng> latLngs = new ArrayList<>();
                                for (GeoPoint geoPoint : locations) {
                                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                    latLngs.add(latLng);
                                }

                                // notify finished
                                callback.onSuccess(latLngs);
                            }
                        }
                    }
                });
    }

    public static void registerCheckinForUser(String eventId, String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts").whereEqualTo("userID", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                // Found the user document, now update it
                                DocumentReference userRef = document.getReference();
                                userRef.update("checkinevents", FieldValue.arrayUnion(eventId))
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's check-in events successfully."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding event to user's check-in events.", e));
                            } else {
                                Log.d(TAG, "No such user document with given userId");
                            }
                        }
                    } else {
                        Log.d(TAG, "Error finding user document: ", task.getException());
                    }
                });
    }

    public static void addListenerToEvent(String eventId, int position, ArrayList<Integer> oldCount, Callback callback) {
        DocumentReference docRef = collection.document(eventId);

        // real-time listener
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    int newCount = ((ArrayList<String>)data.get("checkinCurrent")).size();

                    // Check if the current checkin list has changed
                    if (newCount != oldCount.get(position)) {
                        callback.onSuccessUpdate(newCount);
                    }
                }
            }
        });

    }


    public static void saveQRcode(String qrcodeText, String eventId, Callback callback) {

        // check to see if it is already existed
        collection.whereEqualTo("qrcode", qrcodeText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (!task.getResult().isEmpty()) {
                                callback.onSavedQRCode("QR code already exists. Use another one.");

                            } else {
                                DocumentReference doc = collection.document(eventId);
                                doc.update("qrcode", qrcodeText)
                                        .addOnSuccessListener(aVoid -> callback.onSavedQRCode("successfully saved."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error uploaded qrcode.", e));

                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }





    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public ArrayList<String> getSignup() {
        return signup;
    }

    public void setSignup(ArrayList<String> signup) {
        this.signup = signup;
    }

    public HashMap<String, Integer> getCheckinCounts() {
        return checkinCounts;
    }

    public void setCheckinCounts(HashMap<String, Integer> checkinCounts) {
        this.checkinCounts = checkinCounts;
    }

    public ArrayList<String> getCheckinCurrent() {
        return checkinCurrent;
    }

    public void setCheckinCurrent(ArrayList<String> checkinCurrent) {
        this.checkinCurrent = checkinCurrent;
    }


}

