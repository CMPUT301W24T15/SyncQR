package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity for displaying a list of profiles.
 */
public class ProfileListActivity extends AppCompatActivity {

    private ArrayList<Profile> dataList;
    private ListView profileList;
    private ProfileListAdapter adapter;
    private GestureDetector gestureDetector;

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

        // Gesture Detector to detect swipes
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                            if (diffX > 0) {
                                // Right Swipe
                            } else {
                                // Left Swipe, Trigger Delete Confirmation
                                int position = profileList.pointToPosition((int) e1.getX(), (int) e1.getY());
                                showDeleteConfirmationDialog(position);
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });

        profileList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

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
                // Get and pass userID == profileID
                String profileID = profile.getProfileID();
                Intent intent = new Intent(ProfileListActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                intent.putExtra("userID", profileID);
                startActivity(intent);
            }
        });
    }

    /**
     * Displays a confirmation dialog to delete a profile.
     *
     * @param position The position of the profile in the list to be deleted.
     */
    private void showDeleteConfirmationDialog(final int position) {
        if (position == ListView.INVALID_POSITION) {
            return; // If touch is detected outside of a valid list item, ignore it.
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Deleting the profile from Firestore
                    Profile profile = dataList.get(position);
                    String profileID = profile.getProfileID();
                    ProfileDeletion.deleteProfile(profileID);
                    // Delete the profile from your data list and update the adapter
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
