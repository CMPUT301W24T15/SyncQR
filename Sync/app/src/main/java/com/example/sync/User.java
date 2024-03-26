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

    public User(String userid, String username, String password, Profile profile, String position) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.profile = profile;
        this.position = position;
    }

    public void scanQRCode(){
        new QRCodeScanActivity();
    }

}
