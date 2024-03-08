package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

//        TextView cityName = findViewById(R.id.show_city);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventID = extras.getString("ID");
        }

//        Button backButton = (Button) findViewById(R.id.back_button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

//        Button homeButton = findViewById(R.id.home_button);
//        homeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        Button setNotificationButton = findViewById(R.id.set_Notification_Button);
//        setNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setNotification();
//            }
//        });
//
//        Button browseEventButton = findViewById(R.id.browse_event_button);
//        browseEventButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                browseEvent();
//            }
//        });
    }
}