package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sync.organizer.FragListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    FragListener listener;

    ImageView poster; // has not been implemented. implement when connect to database
    TextView name;
    TextView location;

    TextView date;
//    TextView organizer;
    TextView description;

    String TAG = "EventDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        name = findViewById(R.id.event_name);


        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventID = extras.getString("ID");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("cities").document(eventID);

            final Map<String, Object> data;
            eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            data = document.getData();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            name.setText(data.get("eventName"));

            String event_location = "Location:  " + data.get("eventLocation");
            location.setText(event_location);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            date.setText(sdf.format(data.get("eventDate")));

            String event_description = "Description: " + data.get("eventDescription");
            description.setText(event_description);


//            We dont need organizer according to Figma?

//            String event_organizer = "Organizer:  " + event.getOrganizerName();
//            organizer.setText(data.get("event"));
        }

    }
}