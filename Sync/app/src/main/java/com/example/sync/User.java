package com.example.sync;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public abstract class User extends AppCompatActivity implements EditProfileFragment.OnFragmentInteractionListener{
    private String username;
    private String password;
    private Profile profile;
    private ArrayList<Event> events;

    public User() {
        this.username = "Visitor";
    }

    public void scanQRCode(){
        new QRCodeScanActivity();
    }

}
