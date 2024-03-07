package com.example.sync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProfileAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    public ProfileAdapter(View.OnClickListener context, ArrayList<Profile> profiles){
        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }

        Profile profile = profiles.get(position);

        TextView name = view.findViewById(R.id.profile_name);
        TextView image = view.findViewById(R.id.profile_image);
        TextView email = view.findViewById(R.id.profile_email);
        TextView phoneNumber = view.findViewById(R.id.profile_phone);

        name.setText(profile.getName());
        image.setText(profile.getProfilePictureUrl());
        email.setText(profile.getEmail());
        phoneNumber.setText(profile.getPhoneNumber());

        return view;
    }
}