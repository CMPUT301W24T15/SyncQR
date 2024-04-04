package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity for displaying a list of profiles.
 */
public class ProfileListActivity extends AppCompatActivity {

    private ArrayList<Profile> dataList;
    private ListView profileList;
    private ProfileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profiles);

        // Initialize dataList and profileList
        dataList = new ArrayList<>();
        profileList = findViewById(R.id.profileList);

        // Create and set the adapter
        adapter = new ProfileListAdapter(this, dataList);
        profileList.setAdapter(adapter);

        // Fetch profiles from Firestore and update the UI
        Profile.getAllProfilesFromDatabase(new Profile.Callback() {
            @Override
            public void onSuccess(ArrayList<Profile> profileArrayList) {
                // Update dataList and notify the adapter to refresh the ListView
                dataList.addAll(profileArrayList);
                adapter.notifyDataSetChanged();
            }
        });

        // Set an item click listener on the ListView to handle clicks on individual profiles
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Profile profile = dataList.get(position);
                Intent intent = new Intent(ProfileListActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                // Get and pass userID
                String userID = getIntent().getStringExtra("userID");
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
}
