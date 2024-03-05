package com.example.sync;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Administrator extends User{
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
    private void editProfile(Profile editProfile){
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EditProfileFragment((Profile)profileList.getItemAtPosition(i)).show(getSupportFragmentManager(),"Add/Edit_City");

            }
        });
    }

    @Override
    public void onOKPressed(Profile newCity) {
        profileAdapter.add(newCity);
    }
    }
}
