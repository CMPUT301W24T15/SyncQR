package com.example.sync;

import java.util.ArrayList;
/**
 * This is a class that keeps the attendee's activity
 */
public class Attendee extends User {

    public Attendee(String userID, String username, String password, Profile profile, String position, ArrayList<Event> signupevents, ArrayList<Event> createevents) {
        super(userID, username, password, profile, position, signupevents, createevents);
    }
}
