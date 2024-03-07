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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sync.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreateEventFrag extends Fragment {
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Button upload;
    ImageView image;
    Toolbar toolbar;
    Uri imageuri;
    CreateEventFragListener listener;

    static CreateEventFrag newInstance() {
        // create the fragment instance
        CreateEventFrag fragment = new CreateEventFrag();
        return fragment;
    }

    interface CreateEventFragListener {
        public void notifyShutDown(CreateEventFrag frag);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CreateEventFragListener) {
            listener = (CreateEventFragListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            // 将 InputStream 对象解码为 Bitmap 对象
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            // 设置 Bitmap 对象给 ImageView
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
        image = view.findViewById(R.id.image);
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

    }
}


