package com.example.sync;

import java.util.ArrayList;
/**
 * This is a class that keeps the administrator information
 */
public class Administrator extends User {

    public Administrator(String userID, String username, String password, Profile profile, ArrayList<Event> signupevents, ArrayList<Event> createevents) {
        super(userID, username, password, profile, signupevents, createevents);
    }
}
