package com.example.sync;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.text.TextWatcher;

import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for managing user profile.
 */
public class ProfileActivity extends AppCompatActivity {
    // Class variables
    private EditText userNameInput, userEmailInput, userContactInput;
    private AvatarView userImageInput;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("kevinTag", "Created Profile Instance");

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

        // Get the user ID from the intent
        String userID = getIntent().getStringExtra("userID");

        // Get the profile information for users
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference profileRef = db.collection("Accounts").document(userID);

        profileRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Access the nested 'profile' map
                    Map<String, Object> profile = (Map<String, Object>) document.get("profile");
                    if (profile != null) {
                        // Extract fields from the profile map
                        String name = (String) profile.get("name");
                        String email = (String) profile.get("email");
                        String contact = (String) profile.get("phoneNumber");

                        // Update UI with the retrieved data
                        userNameInput.setText(name);
                        userEmailInput.setText(email);
                        userContactInput.setText(contact);
                        userNameInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // Implementation can be left empty if not needed
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // Implementation can be left empty if not needed
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                String name = s.toString().trim();
                                if (!name.isEmpty()) {
                                    userImageInput.setInitialsFromName(name);
                                } else {
                                    userImageInput.removeInitialsAndImage();
                                }
                            }
                        });
                        if (name != null && !name.isEmpty()) {
                            userImageInput.setInitialsFromName(name);
                        } else {

                            userImageInput.removeInitialsAndImage();
                        }

                        Log.d("DatabaseTag", "Profile data loaded successfully.");
                    } else {
                        Log.d("DatabaseTag", "Profile data is null.");
                    }
                } else {
                    // Document not found
                    Log.d("DatabaseTag", "No profile information found for user ID: " + userID);
                }
            } else {
                Log.d("DatabaseTag", "Error loading profile data", task.getException());
            }
        });


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

    /**
     * Saves the edited profile data to the Firebase Realtime Database.
     * Called when the save button is clicked.
     */

    private void saveProfileData() {
        String name = userNameInput.getText().toString().trim();
        String email = userEmailInput.getText().toString().trim();
        String contact = userContactInput.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the user ID from the intent
        String userID = getIntent().getStringExtra("userID");

        if (userID != null) {
            // Create a map containing the profile fields
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("email", email);
            profileData.put("imageUrl", ""); // Assuming imageUrl is not changed
            profileData.put("name", name);
            profileData.put("phoneNumber", contact);

            // Assuming 'userID' is the document ID in the 'Accounts' collection
            DocumentReference profileRef = db.collection("Accounts").document(userID);

            // Update the 'profile' field with the new profile data
            profileRef.update("profile", profileData, "username", name)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Profile saved successfully");
                        // Optionally, show a success message or navigate to another activity
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save profile", e);
                        // Optionally, show an error message
                    });
        } else {
            Log.e(TAG, "User ID is null");
            // Handle the error, perhaps by informing the user to try again
        }
    }

    /**
     * Navigates to the Attendee activity.
     */
    private void navigateToAttendee() {
        Intent intent = new Intent(this, AttendeeDashboard.class);
        startActivity(intent);
    }

    /**
     * Navigates to the Event activity.
     */
    private void navigateToEvent() {
        Intent intent = new Intent(this, EventListActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the Notification activity.
     */
    private void navigateToMyNotificationReceiver() {
        Intent intent = new Intent(this, NotificationFragment.class);
        startActivity(intent);
    }

    /**
     * Navigates to the QR code scan activity.
     */
    private void navigateToQRCodeScanActivity() {
        Intent intent = new Intent(this, QRCodeScanActivity.class);
        startActivity(intent);
    }

    /**
     * Clears the input fields.
     */
    private void clearInputs() {
        userNameInput.setText("");
        userEmailInput.setText("");
        userContactInput.setText("");
    }

    /**
     * Loads the profile image using Glide library.
     *
     * @param imageUrl The URL of the profile image.
     */
    private void loadProfileImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.uploadimage)
                .error(R.drawable.uploadimage)
                .into(userImageInput);
    }
    /**
     * Opens the image selector to choose a profile image.
     */
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    /**
     * Removes the profile image and sets it to the default image.
     */
    private void removeProfileImage() {
        // Set to default image or clear
        userImageInput.setImageResource(R.drawable.uploadimage); // Assuming 'uploadimage' is your default/placeholder image
    }
    /**
     * Handles the result of picking an image from the device.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent that carries the result data.
     */
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