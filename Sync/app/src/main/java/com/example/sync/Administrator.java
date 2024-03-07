package com.example.sync;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Administrator extends User {
    private ArrayList<Profile> profiles;
    private ListView profileList;
    private ArrayAdapter<Profile> profileArrayAdapter;

    public Administrator(ArrayList<Profile> profiles) {
        this.profiles = profiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        Button confirmButton = findViewById(R.id.administrator_button);
        EditText administratorInput = findViewById(R.id.administrator_text);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your search function here
                searchProfile(String.valueOf(administratorInput));
                profileArrayAdapter = new ProfileAdapter(this, profiles);
                profileList.setAdapter(profileArrayAdapter);
                editProfile();
            }
        });
    }

    private void searchProfile(String profileNumber){
        ArrayList<Profile> find_profiles = Profile.find(profileNumber);
        profiles.addAll(find_profiles);
    }
    public void editProfile(){

        Button editProfileButton = findViewById(R.id.edit_File_Button);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EditProfileFragment((Profile)profileList.getItemAtPosition(i)).show(getSupportFragmentManager(),"Add/Edit_Profile");

            }
        });
    }

    @Override
    public void onConfirmPressed(Profile newProfile) {
        ;
    }
}
