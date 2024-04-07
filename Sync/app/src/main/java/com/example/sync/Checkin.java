package com.example.sync;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.location.Location;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import kotlinx.coroutines.tasks.TasksKt;

/**
 * Checkin System for checking in to an event and storing related information.
 */
public class Checkin {
    private String eventId;
    private String eventName;
    private String qrcodeImage;
    private ArrayList<String> signup;
    private HashMap<String, Integer> checkinCounts;
    private ArrayList<String> checkinCurrent;
    private ArrayList<GeoPoint> geoPoints;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference collection = db.collection("Checkin System");
    private static final String TAG = "Checkin System";

    public interface Callback {
        default void onSuccess(String eventName, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup){}
        default void onSuccess(ArrayList<LatLng> locations){}
        default void onSuccess(GeoPoint geoPoint){}
        default void onSuccessUpdate(ArrayList<Integer> countList){};
    }

    /**
     * Construct a new Checkin system instance
     * @param eventId The event which the checkin system attached to
     */
    public Checkin(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.qrcodeImage = "";
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
        data.put("qrcode",qrcodeImage);
        data.put("signup", signup);
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
                    HashMap <String, Object> counts = (HashMap<String, Object>) document.getData().get("checkinCounts");

                    // handle new value
                    if (counts != null && counts.containsKey(userId)) {
                        long newCount = (long) counts.get(userId) + 1;
                        counts.put(userId, newCount);
                    } else {
                        counts.put(userId, (long) 1);
                    }

                    // update
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
     * Ask permission from the user and acquire their location
     * @param context The context of the app
     * @param callback A callback returned the location once the result sent back
     */
    private static void getLocationFromUser(Context context, Callback callback){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            int MY_PERMISSIONS_REQUEST_LOCATION = 1031;
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    callback.onSuccess(new GeoPoint(latitude, longitude));
                }
            }
        });
    }

    /**
     * When the qr code is scanned, updated the three of the lists at the same time
     * @param context The context of the app
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     */
    public static void checkInForUser(Context context, String eventId, String userId) {
        getLocationFromUser(context, new Callback() {
            @Override
            public void onSuccess(GeoPoint geoPoint) {
                updateLocation(eventId, geoPoint);
            }
        });
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

    public static void addListenerToEvents(String eventId, int position, ArrayList<Integer> countList, Callback callback) {
        DocumentReference docRef = collection.document(eventId);

        // real-time listener
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    int count = ((ArrayList<String>)data.get("checkinCurrent")).size();
                    countList.set(position, count);
                    callback.onSuccessUpdate(countList);
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

    public String getQrcodeImage() {
        return qrcodeImage;
    }

    public void setQrcodeImage(String qrcodeImage) {
        this.qrcodeImage = qrcodeImage;
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

