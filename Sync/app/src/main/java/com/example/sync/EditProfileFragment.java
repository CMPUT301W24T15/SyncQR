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

public class EditProfileFragment extends DialogFragment {

    private EditText name;
    private EditText pictureURL;
    private EditText email;
    private EditText phoneNumber;
    private OnFragmentInteractionListener listener;
    private Profile profile;

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(Profile newProfile);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()+"exception");
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

        if(profile != null){
            name.setText(profile.getName());
            pictureURL.setText(profile.getProfilePictureUrl());
            email.setText(profile.getEmail());
            phoneNumber.setText(profile.getPhoneNumber());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add/Edit new Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newProfileName = name.getText().toString();
                        String newPicture = pictureURL.getText().toString();
                        String newEmail = email.getText().toString();
                        String newPhoneNumber = phoneNumber.getText().toString();
                        listener.onConfirmPressed(new Profile(newProfileName, newPicture, newEmail, newPhoneNumber));

                    }
                }).create();
    }

    public EditProfileFragment(Profile aProfile) {
        super();
        this.profile = aProfile;

    }
    public EditProfileFragment(){
        super();
    }
}
