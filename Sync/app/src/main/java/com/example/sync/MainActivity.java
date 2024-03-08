package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.organizer.OrganizerDashboard;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Log.d("kevinTag", "Created MainActivity Instance");

        Button homeButton = findViewById(R.id.home_button);
        Button profileButton = findViewById(R.id.profile_button);
        Button eventButton = findViewById(R.id.event_button);
        Button messagesButton = findViewById(R.id.messages_button);
        Button checkInButton = findViewById(R.id.check_in_button);
        Button organizerButton = findViewById(R.id.get_into_organizer_Button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Nothing to add, since it stays in homepage
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class); // Assuming you have a ProfileActivity
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
                // not implemented yet
//                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
//                startActivity(intent);
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
                // Define the intent to start the new activity
                Intent intent = new Intent(MainActivity.this, OrganizerDashboard.class);

                // Start the new activity
                startActivity(intent);
            }
        });

    }
}