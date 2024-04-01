package com.example.sync;

public class Profile {
    private static String name;
    private static String imageUrl;
    private static String email;
    private static String phoneNumber;

    // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    public Profile() {
    }

    // Constructor with parameters
    public Profile(String name, String profilePictureUrl, String email, String phoneNumber) {
        this.name = name;
        this.imageUrl = profilePictureUrl;
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
        return imageUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.imageUrl = profilePictureUrl;
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
