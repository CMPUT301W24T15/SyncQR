package com.example.sync;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity representing the dashboard for attendees.
 */
public class AttendeeDashboard extends AppCompatActivity {
    private static String TAG = "Kevin";
    boolean userCheckedIn = FALSE;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_attendee);

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
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
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
                Intent intent = new Intent(AttendeeDashboard.this, CheckinEventListActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

}