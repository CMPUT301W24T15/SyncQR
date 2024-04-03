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

    public User(String userID) {
        this.userID = userID;
        username = "Visitor";
        password = "000000";
        profile = new Profile(username,"https://avatar.iran.liara.run/public","example@com","0000000000");
        position = "Attendee";
        signupevents = new ArrayList<Event>();
        createdevents = new ArrayList<Event>();
    }

    public User(String userID, String username, String password, Profile profile, String position, ArrayList<Event> signupevents, ArrayList<Event> createdevents) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.position = position;
        this.signupevents = signupevents;
        this.createdevents = createdevents;
    }

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
