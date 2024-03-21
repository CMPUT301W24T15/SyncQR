package com.example.sync;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
/**
 * This is a class that contains basic method of users
 */
public abstract class User extends AppCompatActivity{
    private String username;
    private String password;
    private ProfileActivity profile;
    private ArrayList<Event> events;

    public User() {
        this.username = "Visitor";
    }
    /**
     * This is a method to scan QR code
     */
    public void scanQRCode(){
        new QRCodeScanActivity();
    }

}
