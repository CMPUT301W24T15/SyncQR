package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdministratorDashboard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_administrator);
        Button homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start HomeActivity when the button is clicked
                Intent intent = new Intent(AdministratorDashboard.this, MainActivity.class);
                // Consider clearing the task or setting flags if needed, to prevent back stack accumulation
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        Button profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, ProfileActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button NotificationButton = findViewById(R.id.messages_button);
        NotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, NotificationActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button eventButton = findViewById(R.id.event_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, SignUpEventListActivity.class);
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        Button eventBrowseButton = findViewById(R.id.event_browse_button);
        eventBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, AdminEventListActivity.class);
                startActivity(intent);;
            }
        });

        Button profileBrowseButton = findViewById(R.id.profile_browse_button);
        profileBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, ProfileListActivity.class);
                startActivity(intent);;
            }
        });
    }

}