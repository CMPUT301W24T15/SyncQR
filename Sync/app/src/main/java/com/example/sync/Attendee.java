package com.example.sync;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * This is a class that keeps the attendee's activity
 */
public class Attendee extends User {
    private ArrayList<Event> events;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Attendee(String userid, String username, String password, Profile profile, String position) {
        super(userid, username, password, profile, position);
    }
}
