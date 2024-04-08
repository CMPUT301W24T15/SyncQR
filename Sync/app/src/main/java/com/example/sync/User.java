package com.example.sync;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class that contains basic method of users
 */
public class User extends AppCompatActivity {
    private static String TAG = "Kevin";
//    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private String username;
    private String password;
    private Profile profile;
    private ArrayList<Event> signupevents;
    private ArrayList<Event> createdevents;
    private ArrayList<Event> checkinevents;

    /**
     * Constructs a new User object with default values.
     * @param userID The unique identifier for the user.
     */
    public User(String userID) {
        this.userID = userID;
        username = "Visitor";
        password = "";
        profile = new Profile(userID,username,"","","");
        signupevents = new ArrayList<Event>();
        createdevents = new ArrayList<Event>();
        checkinevents = new ArrayList<Event>();
    }

    /**
     * Constructs a new User object with specified values.
     * @param userID The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param profile The profile of the user.
     * @param signupevents The events the user has signed up for.
     * @param createdevents The events the user has created.
     */
    public User(String userID, String username, String password, Profile profile, ArrayList<Event> signupevents, ArrayList<Event> createdevents, ArrayList<Event> checkinevents) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.signupevents = signupevents;
        this.createdevents = createdevents;
        this.checkinevents = checkinevents;
    }

    /**
     * Saves the user data to the database.
     */
    public void saveUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userID", userID);
        userData.put("username", username);
        userData.put("password", password);
        userData.put("profile", profile);
        userData.put("signupevents", signupevents);
        userData.put("createdevents", createdevents);
        userData.put("checkinevents", checkinevents);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts").document(userID).set(userData)
                // Upload successful
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, userID + " Upload Successful");

                })
                // If error occurs during upload
                .addOnFailureListener(e -> {
                    Log.d(TAG, "error occurs during upload");

                });
    }

    // Getters and setters for user properties
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.profile.setName(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileHomepage() {
        return profile.getHomepage();
    }
    public void setProfileHomepage(String homepage) {
        this.profile.setHomepage(homepage);
    }

    public String getProfilePhoneNumber() {
        return profile.getPhoneNumber();
    }
    public void setProfilePhoneNumber(String phoneNumber) {
        this.profile.setPhoneNumber(phoneNumber);
    }

    public ArrayList<Event> getSignUpEvents() {
        return signupevents;
    }

    public void setSignUpEvents(ArrayList<Event> events) {
        this.signupevents = events;
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdevents;
    }

    public void setCreatedEvents(ArrayList<Event> events) {
        this.createdevents = events;
    }

    public ArrayList<Event> getCheckinEvents() {
        return checkinevents;
    }

    public void setCheckinEvents(ArrayList<Event> events) {
        this.checkinevents = events;
    }
}
