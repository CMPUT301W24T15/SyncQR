package com.example.sync;

import androidx.appcompat.app.AppCompatActivity;

public abstract class User extends AppCompatActivity {
    String username;
    String password;

    public User() {
        this.username = "Visitor";
    }

}
