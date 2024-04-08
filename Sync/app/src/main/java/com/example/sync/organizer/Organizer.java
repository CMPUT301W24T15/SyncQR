package com.example.sync.organizer;

import com.example.sync.Event;
import com.example.sync.Profile;
import com.example.sync.User;

import java.util.ArrayList;

/**
 * Organizer class represents a user with organizer privileges.
 * Extends the User class and adds methods specific to organizers.
 */
public class Organizer extends User {

    /**
     * Constructs a new Organizer object.
     * @param userID The unique identifier for the organizer.
     * @param username The username of the organizer.
     * @param password The password of the organizer.
     * @param profile The profile information of the organizer.
     * @param signupevents The list of events the organizer has signed up for.
     * @param createdevents The list of events created by the organizer.
     * @param checkinevents The list of events where the organizer has checked in.
     */
    public Organizer(String userID, String username, String password, Profile profile, ArrayList<Event> signupevents, ArrayList<Event> createdevents, ArrayList<Event> checkinevents) {
        super(userID, username, password, profile, signupevents, createdevents, checkinevents);
    }

    /**
     * Called when the capture state of the pointer associated with a window has changed.
     * @param hasCapture True if the window has captured the pointer, false otherwise.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
