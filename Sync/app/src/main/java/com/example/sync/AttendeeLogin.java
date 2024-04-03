package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;


public class AttendeeLogin extends AppCompatActivity {

    private static String TAG = "Kevin";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button guestLoginButton;
    private Button helpButton;
    private String desiredPosition = "Attendee";


    /**
     * Creates a new instance of the UI components right when the app is started
     * and sets up the event listener for the login and guest login options.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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
            String guestUserID = UserIDGenerator.generateUserID();
            continueAsGuest(guestUserID);
        });
    }


    /**
     * Handles the login progress by checking the provided username and password and comparing it
     * with the accounts in the Firestore Database. If the account is found and the credentials match,
     * then the user is directed to the Attendee Dashboard.
     * Toast messages are diplayed according to if the login was successful or an error occured.
     *
     * This method requires Firebase Database to be connected, otherwise there is no way to retrieve the account.
     *
     * @param username
     * @param password
     *
     */
    private void login(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Accounts")
                .whereEqualTo("username", username)
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
                            String position = (String) data.get("position");
                            if (position.equals(desiredPosition)) {
                                // If login successful, pass the userId to MainActivity
                                Intent intent = new Intent(AttendeeLogin.this, AttendeeDashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                                Toast.makeText(AttendeeLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AttendeeLogin.this, "Login Failed: Incorrect Role.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // Login failed due to user and password not being present

                        Toast.makeText(AttendeeLogin.this, "Login Failed: Incorrect username or password.", Toast.LENGTH_SHORT).show();
                    }
                    // Login failed due to other issues like network or accessing database
                }).addOnFailureListener(e -> Toast.makeText(AttendeeLogin.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void continueAsGuest(String guestUserID) {
        // Create an intent for the MainActivity
        User user = new User(guestUserID);
        user.saveUser();
        Intent intent = new Intent(AttendeeLogin.this, AttendeeDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        // Put the generated guest user ID as an extra in the intent and start activity
        intent.putExtra("userID", guestUserID);
        startActivity(intent);
    }
}
