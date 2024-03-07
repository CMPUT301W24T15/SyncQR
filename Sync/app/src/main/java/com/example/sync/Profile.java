package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {

    private EditText userNameInput, userHomepageInput, userContactInput;
    private DatabaseReference databaseReference;
    private String name;
    private String profilePictureUrl; // This could represent homepage in your context
    private String email; // This could be unused or repurposed in your context
    private String phoneNumber; // This could represent contact in your context

    // Constructor matching your usage
    public Profile(String name, String profilePictureUrl, String email, String phoneNumber) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public static ArrayList<Profile> find(String profileNumber) {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this matches your XML file name

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Initialize UI elements
        userNameInput = findViewById(R.id.user_name_input);
        userHomepageInput = findViewById(R.id.user_homepage_input);
        userContactInput = findViewById(R.id.user_contact_input);
        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button homeButton = findViewById(R.id.home_button);
        Button profileButton = findViewById(R.id.profile_button);
        Button eventButton = findViewById(R.id.event_button);
        Button messagesButton = findViewById(R.id.messages_button);
        Button qrCodeButton = findViewById(R.id.qr_code_button);

        // Set click listeners
        homeButton.setOnClickListener(view -> navigateToAttendee());
        // Profile button does not need a listener as we are already on the profile page
        eventButton.setOnClickListener(view -> navigateToEvent());
        messagesButton.setOnClickListener(view -> navigateToMyNotificationReceiver());
        qrCodeButton.setOnClickListener(view -> navigateToQRCodeScanActivity());

        saveButton.setOnClickListener(view -> saveProfileData());
        cancelButton.setOnClickListener(view -> clearInputs());
    }

    private void navigateToAttendee() {
        Intent intent = new Intent(this, Attendee.class); // Updated to correct class
        startActivity(intent);
    }

    private void navigateToEvent() {
        Intent intent = new Intent(this, Event.class); // Updated to correct class
        startActivity(intent);
    }

    private void navigateToMyNotificationReceiver() {
        Intent intent = new Intent(this, MyNotificationReceiver.class); // Assuming this is an activity you intend to navigate to
        startActivity(intent);
    }

    private void navigateToQRCodeScanActivity() {
        Intent intent = new Intent(this, QRCodeScanActivity.class); // Updated to correct class
        startActivity(intent);
    }

    private void saveProfileData() {
        String name = userNameInput.getText().toString().trim();
        String homepage = userHomepageInput.getText().toString().trim();
        String contact = userContactInput.getText().toString().trim();

        // Assuming you have a Profile object
        String userId = databaseReference.push().getKey(); // Generate a unique ID for the user
        Profile profile = new Profile(name, "", homepage, contact); // Updated to use correct parameters
        databaseReference.child(userId).setValue(profile);
    }

    private void clearInputs() {
        userNameInput.setText("");
        userHomepageInput.setText("");
        userContactInput.setText("");
    }
}
