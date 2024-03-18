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
 * A {@link DialogFragment} subclass used for editing the details of a user profile.
 * It provides a dialog where the user can edit their name, profile picture URL, email,
 * and phone number. The edited profile is then passed back to the activity through
 * an {@link OnFragmentInteractionListener} interface.
 */
public class EditProfileFragment extends DialogFragment {

    private EditText name;
    private EditText pictureURL;
    private EditText email;
    private EditText phoneNumber;

    /**
     * Listener for fragment interaction events.
     */
    private OnFragmentInteractionListener listener;

    /**
     * The profile currently being edited. This could be a new profile or an existing one.
     */
    private ProfileP profileP;

    /**
     * Interface for receiving interaction events.
     * Activities containing this fragment MUST implement this interface.
     */
    public interface OnFragmentInteractionListener {
        /**
         * Called when the OK button is pressed and the profile editing is confirmed.
         *
         * @param newProfileP The updated or newly created {@link ProfileP} object.
         */
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

                        ProfileP updatedProfileP = new ProfileP(newProfileName, newPicture, newEmail, newPhoneNumber);
                        listener.onConfirmPressed(updatedProfileP);
                    }
                }).create();
    }

    /**
     * Constructor for initializing a new profile to edit an existing profile.
     *
     * @param profileP The {@link ProfileP} object to be edited.
     */
    public EditProfileFragment(ProfileP profileP) {
        super();
        this.profileP = profileP;
    }

    /**
     * Use this constructor for creating a new profile without an existing profile.
     */
    public EditProfileFragment() {
        super();
    }
}

