package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.AttendeeDashboard;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OrganizerLogin extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_event_organizer);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        helpButton = findViewById(R.id.helpButtonLogin);


        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            login(username, password);
        });
    }

    private void login(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Redirect to AttendeeDashboard Activity
                            Intent intent = new Intent(OrganizerLogin.this, AttendeeDashboard.class);
                            startActivity(intent);

                            // Toast that login is successful
                            Toast.makeText(OrganizerLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        } else {

                            // Toast that password was incorrect
                            Toast.makeText(OrganizerLogin.this, "Login Failed: Incorrect username or password.", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        // Toast that process failed due to other issues (database connection)
                        Toast.makeText(OrganizerLogin.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}