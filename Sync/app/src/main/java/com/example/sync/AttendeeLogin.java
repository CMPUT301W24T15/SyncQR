package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AttendeeLogin extends AppCompatActivity {

    private static String TAG = "AttendeeLogin";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button guestLoginButton;
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_event_attendee);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        helpButton = findViewById(R.id.helpButtonLogin);
        guestLoginButton = findViewById(R.id.guestButton);

        loginButton.setOnClickListener(view -> {

            // Generate userID for account and pass to login() which will then pass to main activity
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
//            String userId = UserIDGenerator.generateUserID();
            login(username, password);
        });

        guestLoginButton.setOnClickListener(v -> {
            // for guest login, generate userID and pass to continueAsGuest
            String guestUserId = UserIDGenerator.generateUserID();
            continueAsGuest(guestUserId);
        });
    }

    private void login(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Accounts")
                .whereEqualTo("userName", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // record log
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            // Obtain all fields
                            Map<String, Object> data = document.getData();
                            String userID = (String) data.get("userID");

                        // If login successful, pass the userId to MainActivity
                        Intent intent = new Intent(AttendeeLogin.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra("userId", userID);
                        startActivity(intent);
                        Toast.makeText(AttendeeLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Login failed due to user and password not being present
                        Toast.makeText(AttendeeLogin.this, "Login Failed: Incorrect username or password.", Toast.LENGTH_SHORT).show();
                    }
                    // Login failed due to other issues like network or accessing database
                }).addOnFailureListener(e -> Toast.makeText(AttendeeLogin.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void continueAsGuest(String guestUserId) {
        // Create an intent for the MainActivity
        Intent intent = new Intent(AttendeeLogin.this, MainActivity.class);

        // Put the generated guest user ID as an extra in the intent and start activity
        intent.putExtra("userId", guestUserId);
        startActivity(intent);
    }
}
