package com.example.sync;

import static com.example.sync.UserIDGenerator.generateUserID;

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
    private ArrayList<Event> events;

    public User() {
        userID = generateUserID();
        username = "User" + userID;
        password = "";
        profile = new Profile(username,"https://avatar.iran.liara.run/public","example@com","0000000000");
        position = "Attendee";
        events = new ArrayList<Event>();
    }

    public User(String userID) {
        this.userID = userID;
        username = "User" + userID;
        password = "";
        profile = new Profile(username,"https://avatar.iran.liara.run/public","example@com","0000000000");
        position = "Attendee";
        events = new ArrayList<Event>();
    }

    public User(String userid, String username, String password, Profile profile, String position, ArrayList<Event> events) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.position = position;
        this.events = events;
    }

    public void saveUser() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("userID", userID);
        userData.put("username", username);
        userData.put("password", password);
        //userData.put("profile", profile);
        userData.put("position", position);
        userData.put("events", events);


        db.collection("Accounts").document(userID).set(userData)
                // Upload successful
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Upload Successful");

                })
                // If error occurs during upload
                .addOnFailureListener(e -> {
                    Log.d(TAG, "error occurs during upload");

                });
    }

    public void scanQRCode(){
        new QRCodeScanActivity();
    }

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

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
