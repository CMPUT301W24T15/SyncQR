package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EventListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Log.d("kevinTag", "Created EventListActivity Instance");

        Button homeButton = findViewById(R.id.home_button);
        Button profileButton = findViewById(R.id.profile_button);
        Button eventButton = findViewById(R.id.event_button);
        Button messagesButton = findViewById(R.id.messages_button);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, MainActivity.class); // Assuming you have a ProfileActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, Profile.class); // Assuming you have a ProfileActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class); // Assuming you have an EventDetailsActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // not implemented yet
//                Intent intent = new Intent(EventListActivity.this, MessageActivity.class);
//                startActivity(intent);
            }
        });

    }
}
