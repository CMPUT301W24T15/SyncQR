package com.example.sync;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class ProfileActivity extends AppCompatActivity {
    private EditText userNameInput, userEmailInput, userContactInput;
    private ImageView userImageInput;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("kevinTag", "Created Profile Instance");

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts");

        // Initialize UI elements
        userNameInput = findViewById(R.id.user_name_input);
        userImageInput = findViewById(R.id.profile_image_middle);
        userEmailInput = findViewById(R.id.user_email_input);
        userContactInput = findViewById(R.id.user_contact_input);

        userNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used here
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    Bitmap avatar = AvatarGenerator.generateCircleBitmapWithText(Color.BLUE, Color.WHITE, 200, s.toString().substring(0, 1).toUpperCase());
                    userImageInput.setImageBitmap(avatar);
                } else {
                    // Optionally, set a default image if the name is empty
                    userImageInput.setImageResource(R.drawable.default_avatar); // Make sure you have a default_avatar resource
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String userID = getIntent().getStringExtra("userID");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("Accounts").document(userID);

            eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            String nameString = document.getData().get("username").toString();
                            userNameInput.setText(nameString);

                            String email = document.getData().get("email").toString();
                            userEmailInput.setText(email);


                            String contact = document.getData().get("phonenumber").toString();
                            userContactInput.setText(contact);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        String userName = ""; // Assume this is loaded from your data source
        Uri userProfileImageUri = null; // Assume this is loaded from your data source

        if (userProfileImageUri != null) {
            // Load the profile image from the URI
            Glide.with(this).load(userProfileImageUri).into(userImageInput);
        } else if (!userName.isEmpty()) {
            // No profile image, so generate an avatar based on the user's name
            Bitmap avatar = AvatarGenerator.generateCircleBitmapWithText(Color.BLUE, Color.WHITE, 200, userName.substring(0, 1).toUpperCase());
            userImageInput.setImageBitmap(avatar);
        } else {
            // No name set, so use a default placeholder image
            userImageInput.setImageResource(R.drawable.default_avatar);
        }

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

        // userImageInput.setInitialsFromName(name);

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
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void removeProfileImage() {
        // Instead of setting a default image, generate an avatar based on the user's name
        String name = userNameInput.getText().toString().trim();
        if (!name.isEmpty()) {
            Bitmap avatar = AvatarGenerator.generateCircleBitmapWithText(Color.BLUE, Color.WHITE, 200, name.substring(0, 1).toUpperCase());
            userImageInput.setImageBitmap(avatar);
        } else {
            userImageInput.setImageResource(R.drawable.uploadimage); // Fallback to a default image
        }
    }
    private void uploadProfileImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.uploadimage)
                .error(R.drawable.uploadimage)
                .into(userImageInput);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadProfileImage(imageUri.toString());
        } else {
            // Generate an avatar if no image is selected and name is available
            String name = userNameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                Bitmap avatar = AvatarGenerator.generateCircleBitmapWithText(Color.BLUE, Color.WHITE, 200, name.substring(0, 1).toUpperCase());
                userImageInput.setImageBitmap(avatar);
            }
        }
    }
}


