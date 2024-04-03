package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdministratorDashboard extends AppCompatActivity {
    private ArrayList<Profile> profiles = new ArrayList<>();
    private ListView list;
    private ArrayAdapter<Profile> profileArrayAdapter;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayAdapter<Event> eventArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_administrator);

        list = findViewById(R.id.administrator_list); // Make sure you have a ListView with this ID in your layout
        EditText administratorInput = findViewById(R.id.administrator_text);

        // Initialize the ArrayAdapter
        // Assuming Profile class has a toString method overridden to display relevant information
        eventArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, events);
        list.setAdapter(eventArrayAdapter);

        Button eventSearchButton = findViewById(R.id.event_search_button);
        eventSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = administratorInput.getText().toString();
                searchEvent(searchQuery); // Pass the search query to the search function
            }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = events.get(position); // Assuming 'profiles' is your ArrayList<Profile>
            new AlertDialog.Builder(AdministratorDashboard.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this profile?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        removeProfile(String.valueOf(selectedEvent.getEventId()));
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        Button eventBrowseButton = findViewById(R.id.event_browse_button);
        eventBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, EventListActivity.class);
                startActivity(intent);;
            }
        });

        Button profileSearchButton = findViewById(R.id.profile_search_button);

        profileSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = administratorInput.getText().toString();
                searchProfile(searchQuery); // Pass the search query to the search function
            }
        });

        list.setOnItemClickListener((parent, view, position, id) -> {
            Profile selectedProfile = profiles.get(position);
            new AlertDialog.Builder(AdministratorDashboard.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this profile?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        removeProfile(selectedProfile.getName());
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        Button profileBrowseButton = findViewById(R.id.profile_browse_button);
        profileBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdministratorDashboard.this, ProfileListActivity.class);
                startActivity(intent);;
            }
        });
    }

    private void searchProfile(String searchQuery) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profiles")
                .whereEqualTo("name", searchQuery) // Adjust this query as needed
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profiles.clear(); // Clear existing profiles
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Profile profile = document.toObject(Profile.class);
                            profiles.add(profile);
                        }
                        // Notify the adapter to refresh the ListView
                        profileArrayAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("SearchProfile", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void removeProfile(String profileId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Profiles").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("RemoveProfile", "DocumentSnapshot successfully deleted!");
                    // Optionally, refresh your list or UI here if needed
                })
                .addOnFailureListener(e -> Log.w("RemoveProfile", "Error deleting document", e));
    }

    private void searchEvent(String eventid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .whereEqualTo("eventID", eventid) // Adjust this query as needed
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Handle the event document (e.g., display it in UI)
                            Event event = document.toObject(Event.class);
                            Log.d("SearchEvent", "Event found: " + event.getEventName());
                        }
                    } else {
                        Log.d("SearchEvent", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void removeEvent(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("RemoveEvent", "Document successfully deleted!");
                    // Optionally, perform any additional actions after deletion
                })
                .addOnFailureListener(e -> {
                    Log.w("RemoveEvent", "Error deleting document", e);
                    // Handle any errors that occur during deletion
                });
    }


}