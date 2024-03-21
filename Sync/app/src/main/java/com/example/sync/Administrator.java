package com.example.sync;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
/**
 * This is a class that keeps the administrator's activity
 */
public class Administrator extends User {
    private ArrayList<Profile> profiles;
    private ListView profileList;
    private ArrayAdapter<Profile> profileArrayAdapter;

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

    /**
     * This method search for profile and add the searched profile into profiles
     */
    private void searchProfile(String profileNumber){
        ArrayList<Profile> find_profiles = Profile.find(profileNumber);
        profiles.addAll(find_profiles);
    }

    /**
     * This method edit the existing profile through clicking it
     */
    public void editProfile(){
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new EditProfileFragment((Profile)profileList.getItemAtPosition(i)).show(getSupportFragmentManager(),"Add/Edit_Profile");

            }
        });
    }

}
