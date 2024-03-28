package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private EditText userNameInput, userEmailInput, userContactInput;
    private AvatarView userImageInput;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("kevinTag", "Created Profile Instance");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Initialize UI elements
        userNameInput = findViewById(R.id.user_name_input);
        userImageInput = findViewById(R.id.profile_image_middle); // Initialize AvatarView
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

    private void navigateToAttendee() {
        Intent intent = new Intent(this, Attendee.class);
        startActivity(intent);
    }

    private void navigateToEvent() {
        Intent intent = new Intent(this, Event.class);
        startActivity(intent);
    }

    private void navigateToMyNotificationReceiver() {
        Intent intent = new Intent(this, MyNotificationReceiver.class);
        startActivity(intent);
    }

    private void navigateToQRCodeScanActivity() {
        Intent intent = new Intent(this, QRCodeScanActivity.class);
        startActivity(intent);
    }

    private void clearInputs() {
        userNameInput.setText("");
        userEmailInput.setText("");
        userContactInput.setText("");
    }
    private void loadProfileImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.uploadimage)
                .error(R.drawable.uploadimage)
                .into(userImageInput);
    }
}

