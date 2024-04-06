package com.example.sync.organizer;

import com.example.sync.Event;
import com.example.sync.Profile;
import com.example.sync.User;

import java.util.ArrayList;

/**
 * This a class contains the method of organizer use
 */
public class Organizer extends User {


    public Organizer(String userID, String username, String password, Profile profile, String position, ArrayList<Event> signupevents, ArrayList<Event> createdevents) {
        super(userID, username, password, profile, signupevents, createdevents);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}
