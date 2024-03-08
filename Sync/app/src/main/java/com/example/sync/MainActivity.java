package com.example.sync;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Button organizer = findViewById(R.id.get_into_organizer_Button);

        Intent intent = new Intent(MainActivity.this, Administrator.class);
        startActivity(intent);
    }
}