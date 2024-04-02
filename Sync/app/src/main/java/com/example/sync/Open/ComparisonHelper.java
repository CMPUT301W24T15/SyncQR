package com.example.sync.Open;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Helper class for comparing attendee and sign-up numbers for an event.
 */
public class ComparisonHelper {

    /**
     * Interface for listening to the result of the comparison.
     */
    public interface ComparisonListener {
        /**
         * Callback method called when the comparison result is available.
         *
         * @param result true if the attendee number is greater than the sign-up count, false otherwise.
         */
        void onComparisonResult(boolean result);
    }

    /**
     * Compares the number of attendees for an event with the number of sign-ups.
     *
     * @param eventId  The ID of the event to compare.
     * @param listener The listener to handle the result of the comparison.
     */
    public static void compareAttendeeAndSignUpNumbers(String eventId, ComparisonListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get attendee number from events collection
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long attendeeNumber = documentSnapshot.getLong("attendeeNumber");
                    if (attendeeNumber != null) {
                        Log.d("ComparisonHelper", "Attendee Number: " + attendeeNumber);

                        if (attendeeNumber == 0) {
                            // Infinite attendee limit, no need to compare with sign-up count
                            listener.onComparisonResult(true);
                        } else {
                            // Get sign-up count from check-in system
                            db.collection("Checkin System").document(eventId)
                                    .get()
                                    .addOnSuccessListener(signUpSnapshot -> {
                                        if (signUpSnapshot.exists()) {
                                            List<String> signUpList = (List<String>) signUpSnapshot.get("signup");
                                            if (signUpList != null) {
                                                long signUpCount = signUpList.size();
                                                Log.d("ComparisonHelper", "Sign-up Count: " + signUpCount);

                                                // Compare attendee number with sign-up count
                                                boolean result = attendeeNumber > signUpCount;
                                                listener.onComparisonResult(result);
                                            } else {
                                                Log.d("ComparisonHelper", "Sign-up list is null or empty");
                                                listener.onComparisonResult(false);
                                            }
                                        } else {
                                            Log.d("ComparisonHelper", "No sign-up snapshot found");
                                            listener.onComparisonResult(false);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ComparisonHelper", "Error getting sign-up count: " + e.getMessage());
                                        listener.onComparisonResult(false); // Assume failure, prevent sign-up
                                    });
                        }
                    } else {
                        Log.e("ComparisonHelper", "Attendee number is null");
                        listener.onComparisonResult(false); // Assume failure, prevent sign-up
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ComparisonHelper", "Error getting attendee number: " + e.getMessage());
                    listener.onComparisonResult(false); // Assume failure, prevent sign-up
                });
    }

}
