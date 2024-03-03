package com.example.sync;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    String username;
    ArrayList<Integer> password;

    public User() {
        this.username = "Visitor";
    }
}
