package com.example.sync;

public class Profile {
    private String name;
    private String profilePictureUrl;
    private String email;
    private String phoneNumber;

    // Constructor
    public Profile(String name, String profilePictureUrl, String email, String phoneNumber) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
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
