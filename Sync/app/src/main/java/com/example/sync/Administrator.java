package com.example.sync;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
/**
 * This is a class that keeps the administrator's activity
 */
public class Administrator extends User {
    public Administrator(String userid, String username, String password, Profile profile, String position, ArrayList<Event> events) {
        super(userid, username, password, profile, position, events);
    }
}
