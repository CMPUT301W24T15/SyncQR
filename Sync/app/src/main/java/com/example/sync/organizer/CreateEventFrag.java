package com.example.sync.organizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.Attendee;
import com.example.sync.Checkin;
import com.example.sync.Database;
import com.example.sync.Event;
import com.example.sync.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.SimpleFormatter;

public class CreateEventFrag extends Fragment {
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Button create;
    Button upload;
    ImageView image;
    TextInputEditText name;
    TextInputEditText date;
    TextInputEditText location;
    TextInputEditText attendeeNum;
    TextInputEditText description;


    Toolbar toolbar;
    Uri imageuri;
    FragListener listener;

    static CreateEventFrag newInstance() {
        // create the fragment instance
        CreateEventFrag fragment = new CreateEventFrag();
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragListener) {
            listener = (FragListener) context;
        } else {
            throw new RuntimeException(context + " must implement FragListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The above code are from:
        // Mun Bonecci, Jan 9, 2024, Medium
        // https://medium.com/@munbonecci/how-to-display-an-image-loaded-from-the-gallery-using-pick-visual-media-in-jetpack-compose-df83c78a66bf

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    imageuri = uri;
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        try {
                            InputStream inputStream = getContext().getContentResolver().openInputStream(imageuri);
                            image.setImageURI(null);
                            image.setImageURI(imageuri);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_event, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        upload = view.findViewById(R.id.upload_image_button);
        create = view.findViewById(R.id.create);
        image = view.findViewById(R.id.image);

        name = view.findViewById(R.id.name_input);
        date = view.findViewById(R.id.date_input);
        location = view.findViewById(R.id.location_input);
        attendeeNum = view.findViewById(R.id.attendeeNum_input);
        description = view.findViewById(R.id.description_input);
    }

    @Override
    public void onStart() {
        super.onStart();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        CreateEventFrag self = this;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.notifyShutDown(self);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    public void saveEvent(){
        // obtain data
        String nameText = name.getText().toString();
        String dateText = date.getText().toString();
        String locationText = location.getText().toString();
        String attendeeNumText = attendeeNum.getText().toString();
        String descriptionText = description.getText().toString();

        // check data
        if (nameText.isEmpty()) {System.out.println("wrong"); name.setError("Required.");}
        if (dateText.isEmpty()) {date.setError("Required.");}
        if (locationText.isEmpty()) {location.setError("Required.");}
        if (attendeeNumText.isEmpty()){attendeeNumText = "0";}
        if (imageuri == null) {
            int drawableId = R.drawable.welcomeevent;
            imageuri = Uri.parse("android.resource://" + "com.example.sync/" + drawableId);
        }
        String uri = imageuri.toString();


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(dateText);

            // store data into database
            // ********************* change organizer to account ******************
            Timestamp timestamp = new Timestamp(date);
            Event event = new Event(nameText, timestamp, locationText, (long)Integer.parseInt(attendeeNumText),
                    "yiqing", descriptionText, uri, (long)1718521);
            event.saveEventToDatabase();

            // create a checkin system for the event
            Checkin checkin = new Checkin(event.getEventId(), event.getEventName());
            checkin.initializeDatabase();

            // notify user
            Toast.makeText(getContext(), "Successful! View it from Event List!", Toast.LENGTH_LONG).show();
        } catch (ParseException e){
            date.setError("Invalid Format.");
        }




    }
}


