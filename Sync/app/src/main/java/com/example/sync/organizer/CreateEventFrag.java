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

/**
 * This is the fragment invoked to create a new event
 * Can reach this by pressing the button on the organizer dashboard
 * It collects all the event information from the user
 * And store them into firebase as record
 * This is a subclass of fragment
 *
 * @see Fragment
 *
 */
public class CreateEventFrag extends Fragment {

    /**
     * The image picker.
     */
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    /**
     * The create button for saving
     */
    Button create;
    /**
     * The upload button for uploading a poster
     */
    Button upload;
    /**
     * The view of poster
     */
    ImageView image;
    /**
     * The view of event name entered
     */
    TextInputEditText name;
    /**
     * The view of event date entered
     */
    TextInputEditText date;
    /**
     * The view of event location entered
     */
    TextInputEditText location;
    /**
     * The view of organizer name entered
     */
    TextInputEditText organizer;
    /**
     * The view of event limited attendee number
     */
    TextInputEditText attendeeNum;
    /**
     * The view of event description
     */
    TextInputEditText description;


    /**
     * The back button
     */
    Toolbar toolbar;
    /**
     * The poster in bitmap format
     */
    Bitmap imageBitmap;
    /**
     * The listener connected to its parent activity
     */
    FragListener listener;

    /**
     * Create a new instance of the fragment with arguments
     * Call its constructor within the static method
     * @param userId The id of the user
     * @return CreateEventFrag
     */
    static CreateEventFrag newInstance(String userId) {
        // create the fragment instance
        CreateEventFrag fragment = new CreateEventFrag();
        Bundle args = new Bundle();
        args.putString("userID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Attach the listener
     * @param context the context of the application
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragListener) {
            listener = (FragListener) context;
        } else {
            throw new RuntimeException(context + " must implement FragListener");
        }
    }

    /**
     * The process of create a new fragment
     * Initialize a media picker, determine its procedure after image is collected
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
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

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

    }

    /**
     * Inflate a layout
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.sync.R.layout.create_new_event, container, false);
        return view;
    }

    /**
     * Link all the relevant views
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(com.example.sync.R.id.toolbar);
        upload = view.findViewById(com.example.sync.R.id.upload_image_button);
        create = view.findViewById(com.example.sync.R.id.create);
        image = view.findViewById(com.example.sync.R.id.image);

        name = view.findViewById(com.example.sync.R.id.name_input);
        date = view.findViewById(com.example.sync.R.id.date_input);
        location = view.findViewById(com.example.sync.R.id.location_input);
        organizer = view.findViewById(com.example.sync.R.id.organizerName_input);
        attendeeNum = view.findViewById(com.example.sync.R.id.attendeeNum_input);
        description = view.findViewById(com.example.sync.R.id.description_input);
    }

    /**
     * Determine the work procedure after fragment is created
     * Unbindle the argument
     * Set listeners for (back, upload, create) buttons
     * Save the event to datbase
     * @see ActivityResultLauncher<PickVisualMediaRequest>
     */
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

    /**
     * Save the event instance to database
     * @param userId The id of the user who created this event
     * @throws ParseException date failed to convert to the format
     */
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
                    initializeEventProcess(event, userId);

                }
            });
        } else {
            initializeEventProcess(event, userId);
        }



    }

    /**
     * if successfully, initialize all database
     * Event: save the new event into database
     * Checkin: create a new checkin system accompanied with the event
     * Account: record that the user has created a new event (record the eventid)
     * Toast: notify user
     * @param event The event that needs to be stored into database
     * @param userId The id of the user who created this event
     */
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


