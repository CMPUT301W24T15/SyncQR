package com.example.sync;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
/**
 * This is a class that keeps the administrator's activity
 */
public class Administrator extends User {
    private ArrayList<Profile> profiles;
    private ListView profileList;
    private ArrayAdapter<Profile> profileArrayAdapter;

    public Administrator(String userid, String username, String password, Profile profile, String position) {
        super(userid, username, password, profile, position);
    }

}
