package com.example.sync;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

public class Administrator extends User {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        Button confirmButton = findViewById(R.id.administator_button);
        EditText administratorInput = findViewById(R.id.administator_text);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your search function here
                searchProfile(String.valueOf(administratorInput));
            }
        });
    }

    private void searchProfile(String profileNumber){
        Profile profile = Profile.find(profileNumber);

    }
    @Override
    public void onConfirmPressed(Profile newProfile) {
        profileAdapter.add(newProfile);
    }
}
