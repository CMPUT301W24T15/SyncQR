package com.example.sync;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    private Bitmap userBitmap;

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
                        String imageUrl = (String) profile.get("imageUrl"); // Assuming this is where the image URL is stored

                        // Update UI with the retrieved data
                        userNameInput.setText(name);
                        userEmailInput.setText(email);
                        userContactInput.setText(contact);

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Download the profile image as a Bitmap, then store it in Firebase Storage
                            new Thread(() -> {
                                try {
                                    Bitmap bitmap = Glide.with(ProfileActivity.this)
                                            .asBitmap()
                                            .load(imageUrl)
                                            .submit()
                                            .get(); // Download the image as a Bitmap

                                    // Define the path for storing the image in Firebase Storage
                                    String path = "profile/" + userID + ".png";

                                    // Use the Database class to store the image
                                    Database.storeImage(bitmap, path, new Database.Callback() {
                                        @Override
                                        public void onSuccess(String downloadUrl) {
                                            Log.d(TAG, "Image stored successfully with download URL: " + downloadUrl);
                                            // Optionally, update the Firestore document with the new image download URL
                                        }

//                                        @Override
//                                        public void onFailure(Exception e) {
//                                            Log.e(TAG, "Failed to store image", e);
//                                            // Handle the error
//                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Error loading image", e);
                                }
                            }).start();
                        }

                        if (name != null && !name.isEmpty()) {
                            userImageInput.setInitialsFromName(name);
                            userBitmap = getBitmapFromView(userImageInput);
                        } else {
                            userImageInput.removeInitialsAndImage();
                        }

                        Log.d("DatabaseTag", "Profile data loaded successfully.");
                    } else {
                        Log.d("DatabaseTag", "Profile data is null.");
                    }
                } else {
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
        // Get the user ID from the intent
        String userID = getIntent().getStringExtra("userID");

        final String[] url = new String[1];

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference avatarImagesRef = storageRef.child("profile/" + userID + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = avatarImagesRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                avatarImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        // Use this URL for your needs
                        url[0] = downloadUrl.toString();
                        Log.d("Upload", "Upload successful. URL: " + downloadUrl.toString());

                        // Now that you have the URL, update Firestore here
                        String name = userNameInput.getText().toString().trim();
                        String email = userEmailInput.getText().toString().trim();
                        String contact = userContactInput.getText().toString().trim();
                        Map<String, Object> profileData = new HashMap<>();
                        profileData.put("email", email);
                        profileData.put("imageUrl", url[0]); // Now url[0] has the URL
                        profileData.put("name", name);
                        profileData.put("phoneNumber", contact);

                        if (userID != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference profileRef = db.collection("Accounts").document(userID);
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
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("Upload", "Upload failed", exception);
            }
        });
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
        Intent intent = new Intent(this, NotificationActivity.class);
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
        userImageInput.resetToDefault();
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

    public Bitmap getBitmapFromView(View view) {
        // Create a bitmap with the same dimensions as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            // does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

}