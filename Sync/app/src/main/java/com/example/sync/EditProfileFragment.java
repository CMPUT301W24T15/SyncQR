package com.example.sync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * A {@link DialogFragment} subclass used for editing the details of a user profile.
 * It provides a dialog where the user can edit their name, profile picture URL, email,
 * and phone number. The edited profile is then passed back to the activity through
 * an {@link OnFragmentInteractionListener} interface.
 */
public class EditProfileFragment extends DialogFragment {

    private EditText name;
    private EditText email;
    private EditText phoneNumber;
    private ImageView profilePicture;
    private Uri imageUri; // URI of the selected image

    private OnFragmentInteractionListener listener;
    private Profile profile;

    // This launcher will handle picking image from gallery
    private ActivityResultLauncher<String> pickMedia;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickMedia = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            imageUri = uri;
            if (uri != null) {
                profilePicture.setImageURI(uri);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment_layout, null);

        name = view.findViewById(R.id.name_editText);
        email = view.findViewById(R.id.email_editText);
        phoneNumber = view.findViewById(R.id.phone_number_editText);
        profilePicture = view.findViewById(R.id.profile_picture);

        profilePicture.setOnClickListener(v -> {
            // Trigger the image selection
            pickMedia.launch("image/*");
        });

        if (profile != null) {
            name.setText(profile.getName());
            email.setText(profile.getEmail());
            phoneNumber.setText(profile.getPhoneNumber());
            // Assuming you have a method to set the ImageView from a URL
            // profilePicture.setImageURI(Uri.parse(profile.getProfilePictureUrl()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String newProfileName = name.getText().toString();
                    String newEmail = email.getText().toString();
                    String newPhoneNumber = phoneNumber.getText().toString();

                    // Here, instead of directly using the picture URL, you would use the selected imageUri
                    // You might need to upload the image first and get a URL back
                    Profile updatedProfile = new Profile(newProfileName, imageUri.toString(), newEmail, newPhoneNumber);
                    listener.onConfirmPressed(updatedProfile);
                }).create();
    }

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(Profile newProfile);
    }

    /**
     * Constructor for initializing a new profile to edit an existing profile.
     *
     * @param profile The {@link Profile} object to be edited.
     */
    public EditProfileFragment(Profile profile) {
        super();
        this.profile = profile;
    }

    /**
     * Use this constructor for creating a new profile without an existing profile.
     */
    public EditProfileFragment() {
        super();
    }
}

