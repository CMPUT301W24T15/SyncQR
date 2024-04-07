package com.example.sync;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Activity that displays the details of an event for administrators.
 * Users can view event details such as name, location, date, description, and attendee limit.
 * Administrators can also remove the event using the remove button.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {

    ImageView poster; // This ImageView has not been implemented yet. It can be implemented when connecting to a database.
    TextView name;
    TextView location;
    TextView date;
    TextView description;
    TextView limit;

    String TAG = "AdminEventDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_event_detail);

        name = findViewById(R.id.event_name);
        location = findViewById(R.id.event_location);
        date = findViewById(R.id.event_date_time);
        description = findViewById(R.id.event_description);
        limit = findViewById(R.id.event_attendee_limit);
        poster = findViewById(R.id.event_poster);

        Button backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button removeButton = findViewById(R.id.remove_event);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assume eventId is the ID of the event being viewed
                String eventId = getIntent().getStringExtra("eventID");
                // Delete event from database
                Event.deleteEvent(eventId);
            }
        });

        Button removeQRCode = findViewById(R.id.remove_qr_code);
        removeQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete event from database
                showRemoveQRCodeDialog();
            }
        });

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemovePosterDialog();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String eventId = getIntent().getStringExtra("eventID");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference eventRef = db.collection("Events").document(eventId);

            eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            String nameString = document.getData().get("eventName").toString();
                            name.setText(nameString);

                            String event_location = "Location:  " + document.getData().get("eventLocation");
                            location.setText(event_location);

                            Timestamp eventTimestamp = document.getTimestamp("eventDate");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                            String event_Time = sdf.format(eventTimestamp.toDate());
                            date.setText(event_Time);

                            String event_description = "Description: " + document.getData().get("eventDescription");
                            description.setText(event_description);

                            String attendee_limit = "Attendee Limit: " + document.getData().get("attendeeNumber").toString();
                            limit.setText(attendee_limit);

                            if (!Objects.equals(document.getData().get("poster"),"")){
                                Glide.with(AdminEventDetailsActivity.this)
                                        .load(document.getData().get("poster"))
                                        .into(poster);
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    private void showRemovePosterDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Poster") // Set the dialog title
                .setMessage("Do you want to remove the poster?") // Set the message to show in the dialog
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String eventId = getIntent().getStringExtra("eventID");
                        Event.removePoster(eventId); // Call method to remove the poster
                    }
                })
                .setNegativeButton("No", null) // No action, just close the dialog
                .show();
    }

    private void showRemoveQRCodeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Remove QR Code") // Set the dialog title
                .setMessage("Do you want to remove the check-in QR code?") // Set the message to show in the dialog
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String eventId = getIntent().getStringExtra("eventID");
                        Checkin.removeQRCode(eventId); // Call method to remove the qr code
                    }
                })
                .setNegativeButton("No", null) // No action, just close the dialog
                .show();
    }
}
