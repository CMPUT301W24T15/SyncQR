package com.example.sync;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for deleting a profile from the Firestore database.
 */
public class ProfileDeletion {

    // Firestore database instance
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Deletes a profile from the Firestore database.
     *
     * @param profileID The ID of the profile to be deleted.
     */
    public static void deleteProfile(String profileID) {
        // Get the reference to the document containing the profile in the Firestore database
        DocumentReference profileRef = db.collection("Accounts").document(profileID);

        // Create a map representing the new blank profile data
        Map<String, Object> blankProfileData = new HashMap<>();
        blankProfileData.put("profileID", profileID);
        blankProfileData.put("name", "");
        blankProfileData.put("imageUrl", "");
        blankProfileData.put("homepage", "");
        blankProfileData.put("phoneNumber", "");

        // Update the profile field with the new blank profile data
        profileRef.update("profile", blankProfileData)
                .addOnSuccessListener(aVoid -> {
                    // Profile update successful
                    System.out.println("Profile deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    // Handle any errors or exceptions
                    System.out.println("Error deleting profile: " + e.getMessage());
                });
    }
}
