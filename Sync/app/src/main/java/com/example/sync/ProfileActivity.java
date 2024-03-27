package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
/**
 * This is a class that contains the methods of profile
 */
public class ProfileActivity extends AppCompatActivity {
    private EditText userNameInput, userEmailInput, userContactInput;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure this matches your XML file name

        Log.d("kevinTag", "Created Profile Instance");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Initialize UI elements
        userNameInput = findViewById(R.id.user_name_input);
        userEmailInput = findViewById(R.id.user_email_input);
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
    private void saveProfileData() {
        String name = userNameInput.getText().toString().trim();
        String email = userEmailInput.getText().toString().trim();
        String contact = userContactInput.getText().toString().trim();

        String userId = databaseReference.push().getKey();
        Profile profile = new Profile(name, "", email, contact);
        databaseReference.child(userId).setValue(profile);
    }
    /**
     * This is a method that goes to attendee page
     */
    private void navigateToAttendee() {
        Intent intent = new Intent(this, Attendee.class); // Updated to correct class
        startActivity(intent);
    }
    /**
     * This is a method that goes to events page
     */
    private void navigateToEvent() {
        Intent intent = new Intent(this, Event.class); // Updated to correct class
        startActivity(intent);
    }
    /**
     * This is a method that goes to notification page
     */
    private void navigateToMyNotificationReceiver() {
        Intent intent = new Intent(this, MyNotificationReceiver.class); // Assuming this is an activity you intend to navigate to
        startActivity(intent);
    }
    /**
     * This is a method that goes to QRCode scan page
     */
    private void navigateToQRCodeScanActivity() {
        Intent intent = new Intent(this, QRCodeScanActivity.class); // Updated to correct class
        startActivity(intent);
    }

    private void clearInputs() {
        userNameInput.setText("");
        userEmailInput.setText("");
        userContactInput.setText("");
    }
    /**
     * These are methods of getters and setters
     */

}

