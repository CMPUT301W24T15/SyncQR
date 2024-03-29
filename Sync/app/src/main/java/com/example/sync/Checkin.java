package com.example.sync;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class Checkin {
    private String eventId;
    private String qrcodeImage;
    private ArrayList<String> signup;
    private HashMap<String, Integer> checkinCounts;
    private ArrayList<String> checkinCurrent;
    private HashMap<String, LatLng> location;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference collection = db.collection("Checkin System");
    private static final String TAG = "Checkin System";

    /**
     * Construct a new Checkin system instance
     * @param eventId The event which the checkin system attached to
     */
    public Checkin(String eventId) {
        this.eventId = eventId;
        this.qrcodeImage = "";
        this.signup = new ArrayList<>();
        this.checkinCounts = new HashMap<>();
        this.checkinCurrent = new ArrayList<>();
        this.location = new HashMap<>();
    }

    /**
     * Initialize a Checkin System document in Firebase
     * @see CollectionReference
     */
    public void initializeDatabase() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("eventId", eventId);
        data.put("qrcode",qrcodeImage);
        data.put("signup", signup);
        data.put("checkinCounts", checkinCounts);
        data.put("checkinCurrent", checkinCurrent);
        data.put("location", location);

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

    public HashMap<String, LatLng> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, LatLng> location) {
        this.location = location;
    }

}

