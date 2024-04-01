package com.example.sync;

public class Profile {
    private String name;
    private String imageUrl;
    private String email;
    private String phoneNumber;

    // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    public Profile() {
    }

    // Constructor with parameters
    public Profile(String name, String imageUrl, String email, String phoneNumber) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
