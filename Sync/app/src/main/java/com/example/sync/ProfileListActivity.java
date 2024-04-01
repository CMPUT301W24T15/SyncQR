package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ProfileListActivity extends AppCompatActivity {

    private ArrayList<Profile> profileList;
    private ListView listView;
    private ProfileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profiles);

        profileList = new ArrayList<>();
        listView = findViewById(R.id.profileList);
        adapter = new ProfileListAdapter(this, profileList);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        // Set item click listener to handle clicks on individual profiles
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Profile profile = profileList.get(position);
                // Start a new activity to show profile details, passing necessary data
                Intent intent = new Intent(ProfileListActivity.this, ProfileActivity.class);
                intent.putExtra("profile", (CharSequence) profile);
                startActivity(intent);
            }
        });
    }
}
