package com.example.sync;

public class Notification {
    private String id; // Unique identifier for each notification
    private String title;
    private String message;

    public Notification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
}

