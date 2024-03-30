package com.example.sync;

import static com.google.firebase.firestore.FieldValue.arrayRemove;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkin {
    private String eventId;
    private String qrcodeImage;
    private ArrayList<String> signup;
    private HashMap<String, Integer> checkinCounts;
    private ArrayList<String> checkinCurrent;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference collection = db.collection("Checkin System");
    private static final String TAG = "Checkin System";

    public interface Callback {
        void onSuccess(Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup);
    }

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
                    HashMap<String, Object> counts = document.get("checkinCounts", HashMap.class);

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
     * When the qr code is scanned, updated the two of the lists at the same time
     * @param eventId The id of the correspondent event
     * @param userId The id of the correspondent user
     */
    public static void checkInForUser(String eventId, String userId) {
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
                    HashMap <String, Object> counts = (HashMap<String, Object>) document.getData().get("checkinCounts");
                    ArrayList<String> current = (ArrayList<String>) document.getData().get("checkinCurrent");
                    ArrayList<String> signup = (ArrayList<String>) document.getData().get("signup");

                    callback.onSuccess(counts, current, signup);
                }
            }
        });
    }

    /**
     * Save the generated qr code to the database
     * @param eventId The id of the correspondent event
     * @param uri The uri of the qrcode
     */

    public static void saveQRcodeToDatabase(String eventId, Uri uri) {
        String qrcode = uri.toString();
        DocumentReference doc = collection.document(eventId);
        doc.update("qrcode", qrcode)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Update qrcode successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding qrcode.", e));
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

