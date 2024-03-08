package com.example.sync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class shows an edit profile fragment
 */
public class EditProfileFragment extends DialogFragment {

    private EditText name;
    private EditText pictureURL;
    private EditText email;
    private EditText phoneNumber;
    private OnFragmentInteractionListener listener;
    private ProfileP profileP; // Use ProfileP instead of Profile

    // Update the interface to use ProfileP
    public interface OnFragmentInteractionListener {
        void onConfirmPressed(ProfileP newProfileP);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment_layout, null);

        name = view.findViewById(R.id.name_editText);
        pictureURL = view.findViewById(R.id.pictureurl_editText);
        email = view.findViewById(R.id.email_editText);
        phoneNumber = view.findViewById(R.id.phone_number_editText);

        // Initialize fields if editing an existing profile
        if (profileP != null) {
            name.setText(profileP.getName());
            pictureURL.setText(profileP.getProfilePictureUrl());
            email.setText(profileP.getEmail());
            phoneNumber.setText(profileP.getPhoneNumber());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newProfileName = name.getText().toString();
                        String newPicture = pictureURL.getText().toString();
                        String newEmail = email.getText().toString();
                        String newPhoneNumber = phoneNumber.getText().toString();

                        // Create or update ProfileP instance
                        ProfileP updatedProfileP = new ProfileP(newProfileName, newPicture, newEmail, newPhoneNumber);
                        listener.onConfirmPressed(updatedProfileP);
                    }
                }).create();
    }

    // Constructor for initializing with an existing ProfileP (for editing)
    public EditProfileFragment(ProfileP profileP) {
        super();
        this.profileP = profileP;
    }

    // Default constructor
    public EditProfileFragment() {
        super();
    }
}
