package com.example.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.organizer.OrganizerDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * class for Login Page
 */

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "KevinTag";
    private String userID;

    /**
     * Method to be called when the activity is first created
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // read userID from local setting
        userID = sharedPref.getString("userID", null);

        // if userID is null, it is a new user
        if (userID == null) {
            userID = UserIDGenerator.generateUserID();
            User user = new User(userID);
            user.saveUser();
            Log.d(TAG, "Creating new Account in Login Activity");
            editor.putString("userID", userID);
            editor.apply();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Accounts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> allUserIds = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve profile data
                                Map<String, Object> data = document.getData();
                                allUserIds.add((String)data.get("userID"));
                                Log.d(TAG, (String)data.get("userID") + " Found");
                            }
                        }
                        boolean found = false;
                        for (String s : allUserIds) {
                            if (userID.equals(s))
                                found = true;
                        }
                        if (!found) {
                            userID = UserIDGenerator.generateUserID();
                            User user = new User(userID);
                            user.saveUser();
                            Log.d(TAG, "Creating new Account in Login Activity");
                            editor.putString("userID", userID);
                            editor.apply();
                        }
                    }
                });



        Log.d("kevinTag", "Created Login Activity");

        Button attendeeLoginButton = findViewById(R.id.login_attendee_button);
        Button organizerLoginButton = findViewById(R.id.login_organizer_button);
        Button administratorLoginButton = findViewById(R.id.login_administrator_button);

        attendeeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AttendeeDashboard.class);
                intent.putExtra("userID", userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        organizerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, OrganizerDashboard.class);
                intent.putExtra("userID", userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        administratorLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, QRCodeScanActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
}
