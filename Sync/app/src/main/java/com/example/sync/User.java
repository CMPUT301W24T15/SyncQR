package com.example.sync;

import static com.example.sync.UserIDGenerator.generateUserID;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
/**
 * This is a class that contains basic method of users
 */
public abstract class User extends AppCompatActivity {
    private String userid = generateUserID();
    private String username = "Visitor";
    private String password = "000000"; //Initial Password
    private Profile profile = new Profile(username,"https://avatar.iran.liara.run/public","example@com","0000000000");
    private String position = "Attendee";
    private ArrayList<Event> events;

    public User(String userid, String username, String password, Profile profile, String position, ArrayList<Event> events) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.position = position;
        this.events = events;
    }

    public void scanQRCode(){
        new QRCodeScanActivity();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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
