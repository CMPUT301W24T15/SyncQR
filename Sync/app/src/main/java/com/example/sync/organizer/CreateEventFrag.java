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
import java.io.IOException;
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
    TextInputEditText organizer;
    TextInputEditText attendeeNum;
    TextInputEditText description;


    Toolbar toolbar;
    Bitmap imageBitmap;
    FragListener listener;

    static CreateEventFrag newInstance(String userId) {
        // create the fragment instance
        CreateEventFrag fragment = new CreateEventFrag();
        Bundle args = new Bundle();
        args.putString("userID", userId);
        fragment.setArguments(args);
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
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        try {
                            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                            image.setImageURI(null);
                            image.setImageURI(uri);
                            imageBitmap = BitmapFactory.decodeStream(inputStream);
                            inputStream.close();

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
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
        organizer = view.findViewById(R.id.organizerName_input);
        attendeeNum = view.findViewById(R.id.attendeeNum_input);
        description = view.findViewById(R.id.description_input);
    }

    @Override
    public void onStart() {
        super.onStart();

        // acquire userID
        Bundle args = getArguments();
        assert args != null;
        String userId = args.getString("userID");

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
                try {
                    saveEvent(userId);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void saveEvent(String userId) throws ParseException {
        // obtain data
        String nameText = name.getText().toString();
        String dateText = date.getText().toString();
        String locationText = location.getText().toString();
        String organizerText = organizer.getText().toString();
        String attendeeNumText = attendeeNum.getText().toString();
        String descriptionText = description.getText().toString();

        // check data
        if (nameText.isEmpty()) {System.out.println("wrong"); name.setError("Required.");}
        if (dateText.isEmpty()) {date.setError("Required.");}
        if (locationText.isEmpty()) {location.setError("Required.");}
        if (attendeeNumText.isEmpty()){attendeeNumText = "0";}


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = sdf.parse(dateText);

        // store data into database
        // ********************* change organizer to account ******************
        Timestamp timestamp = new Timestamp(date);
        Event event = new Event(nameText, timestamp, locationText, (long)Integer.parseInt(attendeeNumText),
                organizerText, descriptionText, "", userId);

        // store poster
        if (imageBitmap != null) {
            String path = "poster/" + event.getEventId() + ".png";
            Database.storeImage(imageBitmap, path, new Database.Callback() {
                @Override
                public void onSuccess(String uri) {
                    event.setPoster(uri);

                    // if successfully, initialize all database
                    // Event: save the new event into database
                    // Checkin: create a new checkin system accompanied with the event
                    // Account: record that the user has created a new event (record the eventid)
                    // Toast: notify user

                    initializeEventProcess(event, userId);

                }
            });
        } else {
            initializeEventProcess(event, userId);
        }



    }

    public void initializeEventProcess(Event event, String userId){
        // save event
        event.saveEventToDatabase();

        // create a checkin system for the event
        Checkin checkin = new Checkin(event.getEventId(), event.getEventName());
        checkin.initializeDatabase();

        // record history for user
        Event.registerInAccount(userId, event.getEventId());

        // notify user
        Toast.makeText(getContext(), "Successful! View it from Event List!", Toast.LENGTH_LONG).show();
    }

}


