package com.example.sync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sync.organizer.OrganizerDashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Storage for user information
    private List<String> signedInUsers = new ArrayList<>();
    private Map<String, String> checkInUsers = new HashMap<>();
    private List<String> currentCheckInUsers = new ArrayList<>();
    private Map<String, String> locations = new HashMap<>();
    private int userIdSequence = 0;

    // SharedPreferences for persistence
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        loadUserData();

        // Setup buttons
        setupButtons();
    }

    private void setupButtons() {
        Button homeButton = findViewById(R.id.home_button);
        Button profileButton = findViewById(R.id.profile_button);
        Button eventButton = findViewById(R.id.event_button);
        Button messagesButton = findViewById(R.id.messages_button);
        Button checkInButton = findViewById(R.id.check_in_button);
        Button organizerButton = findViewById(R.id.get_into_organizer_Button);

        // Define button click listeners here as before
        // For checkInButton, you'll need to handle the QR code scanning and subsequent logic
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for now, linked to attendee
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventListActivity.class); // Assuming you have an EventDetailsActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for now, linked to administrator
                Intent intent = new Intent(MainActivity.this, Administrator.class);
                startActivity(intent);
            }
        });

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRCodeScanActivity.class); // Assuming you have a QRCodeScanActivity
                startActivity(intent);
            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for now, linked to organizer
                Intent intent = new Intent(MainActivity.this, OrganizerDashboard.class);

                // Start the new activity
                startActivity(intent);
            }
        });
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming QR code scanning is handled within QRCodeScanActivity and returns a result
                Intent intent = new Intent(MainActivity.this, QRCodeScanActivity.class);
                startActivityForResult(intent, 1); // Use requestCode 1 for check-in result
            }
        });

        // Other button setups remain unchanged
    }

    // Handle the QR code scanning result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String userName = data.getStringExtra("userName"); // Assuming the QR code contains the userName
            if (!signedInUsers.contains(userName)) {
                signedInUsers.add(userName);
            }
            String userId = "User_" + (++userIdSequence);
            checkInUsers.put(userId, userName);
            currentCheckInUsers.add(userName);
            // Ask for location permission here and handle location update

            saveUserData(); // Save updated user data
        }
    }

    private void loadUserData() {
        // Load and deserialize user data from SharedPreferences
        String signedInUsersJson = sharedPreferences.getString("SignedInUsers", "");
        String checkInUsersJson = sharedPreferences.getString("CheckInUsers", "");
        String currentCheckInUsersJson = sharedPreferences.getString("CurrentCheckInUsers", "");
        String locationsJson = sharedPreferences.getString("Locations", "");

        Type listType = new TypeToken<List<String>>() {}.getType();
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();

        signedInUsers = gson.fromJson(signedInUsersJson, listType);
        checkInUsers = gson.fromJson(checkInUsersJson, mapType);
        currentCheckInUsers = gson.fromJson(currentCheckInUsersJson, listType);
        locations = gson.fromJson(locationsJson, mapType);

        if (signedInUsers == null) signedInUsers = new ArrayList<>();
        if (checkInUsers == null) checkInUsers = new HashMap<>();
        if (currentCheckInUsers == null) currentCheckInUsers = new ArrayList<>();
        if (locations == null) locations = new HashMap<>();
    }

    private void saveUserData() {
        // Serialize and save user data to SharedPreferences
        editor.putString("SignedInUsers", gson.toJson(signedInUsers));
        editor.putString("CheckInUsers", gson.toJson(checkInUsers));
        editor.putString("CurrentCheckInUsers", gson.toJson(currentCheckInUsers));
        editor.putString("Locations", gson.toJson(locations));
        editor.apply();
    }
}
