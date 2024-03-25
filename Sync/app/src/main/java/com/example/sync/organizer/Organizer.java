package com.example.sync.organizer;

import com.example.sync.Profile;
import com.example.sync.User;
/**
 * This a class contains the method of organizer use
 */
public class Organizer extends User {
    public Organizer(String userid, String username, String password, Profile profile, String position) {
        super(userid, username, password, profile, position);
    }

    public void createNewEvent(){
        int i = 0;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
        int i = 0;
    }

}
