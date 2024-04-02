package com.example.sync;

/**
 * Profile represents user profile information.
 */
public class Profile {
    private String name;
    private String imageUrl;
    private String email;
    private String phoneNumber;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Profile.class).
     */
    public Profile() {
    }

    /**
     * Constructor to initialize profile information.
     *
     * @param name        The name of the user.
     * @param imageUrl    The URL of the user's profile image.
     * @param email       The email address of the user.
     * @param phoneNumber The phone number of the user.
     */
    public Profile(String name, String imageUrl, String email, String phoneNumber) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the user.
     *
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the URL of the user's profile image.
     *
     * @return The URL of the user's profile image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Set the URL of the user's profile image.
     *
     * @param imageUrl The URL of the user's profile image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Get the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email address of the user.
     *
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the phone number of the user.
     *
     * @return The phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set the phone number of the user.
     *
     * @param phoneNumber The phone number of the user.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
