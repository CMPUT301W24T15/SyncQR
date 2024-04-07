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
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;
    private String username;
    private String password;
    private Profile profile;
    private String position;
    private ArrayList<Event> signupevents;
    private ArrayList<Event> createdevents;

    /**
     * Constructs a new User object with default values.
     * @param userID The unique identifier for the user.
     */
    public User(String userID) {
        this.userID = userID;
        username = "Visitor";
        password = "";
        profile = new Profile(userID,username,"https://avatar.iran.liara.run/public","","");
        position = "Attendee";
        signupevents = new ArrayList<Event>();
        createdevents = new ArrayList<Event>();
    }

    /**
     * Constructs a new User object with specified values.
     * @param userID The unique identifier for the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param profile The profile of the user.
     * @param position The position of the user.
     * @param signupevents The events the user has signed up for.
     * @param createdevents The events the user has created.
     */
    public User(String userID, String username, String password, Profile profile, String position, ArrayList<Event> signupevents, ArrayList<Event> createdevents) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.position = position;
        this.signupevents = signupevents;
        this.createdevents = createdevents;
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
        userData.put("position", position);
        userData.put("signupevents", signupevents);
        userData.put("createdevents", createdevents);


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

    /**
     * Initiates the QR code scanning activity.
     */
    public void scanQRCode(){
        new QRCodeScanActivity();
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

    public String getProfileEmail() {
        return profile.getEmail();
    }
    public void setProfileEmail(String email) {
        this.profile.setEmail(email);
    }

    public String getProfilePhoneNumber() {
        return profile.getPhoneNumber();
    }
    public void setProfilePhoneNumber(String phoneNumber) {
        this.profile.setPhoneNumber(phoneNumber);
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
}
