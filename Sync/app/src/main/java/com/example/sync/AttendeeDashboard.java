package com.example.sync;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import com.example.sync.organizer.QRCodeShareActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity representing the dashboard for attendees.
 */
public class AttendeeDashboard extends AppCompatActivity implements LocationPermissionDialog.LocationPermissionDialogListener {
    private static String TAG = "Kevin";
    boolean userCheckedIn = FALSE;
    private String userID;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_attendee);
        Button permissionButton = findViewById(R.id.permission_button);
        permissionButton.setOnClickListener(v -> new LocationPermissionDialog().show(getSupportFragmentManager(), "LocationPermissionDialog"));

        Log.d(TAG, "Entered AttendeeDashboard");

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            userID = getIntent().getStringExtra("userID");
        }

        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // left blank since it's in the activity already
            }
        });

        Button profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, ProfileActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button NotificationButton = findViewById(R.id.messages_button);
        NotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, NotificationActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button eventButton = findViewById(R.id.event_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, SignUpEventListActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button qrCodeButton = findViewById(R.id.qr_code_button);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, QRCodeShareActivity.class);
                String inputID = getIntent().getStringExtra("userID");
                intent.putExtra("inputID", inputID);
                startActivity(intent);
            }

        });

        Button checkInButton = findViewById(R.id.check_in_with_qr_button);
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCheckedIn = TRUE;
                Button quitEventButton = findViewById(R.id.quit_event_button);
                if (userCheckedIn) {
                    quitEventButton.setVisibility(View.VISIBLE);
                } else {
                    quitEventButton.setVisibility(View.GONE);
                }
                Intent intent = new Intent(AttendeeDashboard.this, QRCodeScanActivity.class);
                startActivity(intent);
            }
        });

        Button browseEventButton = findViewById(R.id.browse_event_button);
        browseEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeDashboard.this, EventListActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }

        });
        Button quitEventButton = findViewById(R.id.quit_event_button);
        quitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the user's ID from the check-in system
                removeFromCheckInSystem(userID);
            }
        });
    }
    private static final int YOUR_PERMISSION_REQUEST_CODE = 123;

    // Chatgpt, OpenAI. Downloaded 2024-03-29
    // Implement the interface method
    @Override
    public void onRequestLocationPermission() {
        // Check for permission or request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, YOUR_PERMISSION_REQUEST_CODE);
        }
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Requests location permission from the user.
     * If permission is already granted, nothing happens. Otherwise, a permission request dialog is displayed.
     */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Explain to the user why the permission is needed
            // Then request the permission
            ActivityCompat.requestPermissions(AttendeeDashboard.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                // You can now use the location services
            } else {
                // Permission denied
                // Disable the functionality that depends on this permission.
            }
        }
    }

    private void removeFromCheckInSystem(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference checkInRef = db.collection("Check-in System").document(userID);

        // Remove the user's ID from the "signup" field
        checkInRef.update("signup", FieldValue.arrayRemove(userID))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User removed from check-in system successfully."))
                .addOnFailureListener(e -> Log.w(TAG, "Error removing user from check-in system.", e));
    }

}