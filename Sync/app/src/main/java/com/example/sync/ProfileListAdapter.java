package com.example.sync;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProfileListAdapter extends ArrayAdapter<Profile> {
    private Activity context;
    private ArrayList<Profile> profiles;

    public ProfileListAdapter(Activity context, ArrayList<Profile> profiles) {
        super(context, R.layout.profile_list_item, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = convertView;
        if (convertView == null) {
            listViewItem = inflater.inflate(R.layout.profile_list_item, null, true);
        }

        TextView textViewUsername = listViewItem.findViewById(R.id.profile_name);
        ImageView userImage = listViewItem.findViewById(R.id.profile_Image);
        TextView textViewEmail = listViewItem.findViewById(R.id.profile_email);
        TextView textViewPhoneNumber = listViewItem.findViewById(R.id.profile_phone_number);

        Profile profile = profiles.get(position);
        textViewUsername.setText(profile.getName());
        Glide.with(context)
                .load(profile.getImageUrl())
                .into(userImage);
        textViewEmail.setText(profile.getEmail());
        textViewPhoneNumber.setText(profile.getPhoneNumber());

        return listViewItem;
    }
}

