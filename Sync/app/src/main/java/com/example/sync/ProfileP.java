package com.example.sync;

public class ProfileP {
    private static String name;
    private static String profilePictureUrl;
    private static String email;
    private static String phoneNumber;

    // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    public ProfileP() {
    }

    // Constructor with parameters
    public ProfileP(String name, String profilePictureUrl, String email, String phoneNumber) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public static String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
