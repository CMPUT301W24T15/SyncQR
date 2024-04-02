package com.example.sync;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private EditText userNameInput, userEmailInput, userContactInput;
    private AvatarView userImageInput;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
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
        Button uploadpicButton = findViewById(R.id.upload_picture_button);
        Button removepicButton = findViewById(R.id.remove_picture_button);


        // Set click listeners
        homeButton.setOnClickListener(view -> navigateToAttendee());
        eventButton.setOnClickListener(view -> navigateToEvent());
        messagesButton.setOnClickListener(view -> navigateToMyNotificationReceiver());
        qrCodeButton.setOnClickListener(view -> navigateToQRCodeScanActivity());

        uploadpicButton.setOnClickListener(view -> openImageSelector());
        removepicButton.setOnClickListener(view -> removeProfileImage());
        saveButton.setOnClickListener(view -> saveProfileData());
        cancelButton.setOnClickListener(view -> clearInputs());
    }

    private void saveProfileData() {
        String name = userNameInput.getText().toString().trim();
        String email = userEmailInput.getText().toString().trim();
        String contact = userContactInput.getText().toString().trim();
        userImageInput.removeInitialsAndImage();
        userImageInput.setInitialsFromName(name);

        String userId = databaseReference.push().getKey();
        if (userId != null) {
            Profile profile = new Profile(name, imageUri != null ? imageUri.toString() : "", email, contact);
            // Assuming you have a way to handle image URIs in your Profile class
            // This assumes 'imageUri' is the Uri of the uploaded image or null if no image has been uploaded
            databaseReference.child(userId).setValue(profile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Profile saved successfully");
                            // Optionally, show a success message or navigate to another activity
                        } else {
                            Log.e(TAG, "Failed to save profile", task.getException());
                            // Optionally, show an error message
                        }
                    });
        } else {
            Log.e(TAG, "Failed to generate a unique key for the profile");
            // Handle the error, perhaps by informing the user to try again
        }
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
        Intent intent = new Intent(this, NotificationFragment.class);
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
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void removeProfileImage() {
        // Set to default image or clear
        userImageInput.setImageResource(R.drawable.uploadimage); // Assuming 'uploadimage' is your default/placeholder image
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Assuming loadImage is a method to load image from URI using Glide or similar
            userImageInput.setImageUri(imageUri); // Update this line to use the new method
        }
    }



}