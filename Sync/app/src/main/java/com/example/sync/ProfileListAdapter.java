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

/**
 * Custom ArrayAdapter implementation for displaying a list of profiles in a ListView.
 */
public class ProfileListAdapter extends ArrayAdapter<Profile> {
    private Activity context;
    private ArrayList<Profile> profiles;

    /**
     * Constructor for ProfileListAdapter.
     * @param context The context where the ListView is being displayed.
     * @param profiles The list of profiles to be displayed in the ListView.
     */
    public ProfileListAdapter(Activity context, ArrayList<Profile> profiles) {
        super(context, R.layout.profile_list_item, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    /**
     * Returns a View representing the data at the specified position in the adapter.
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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


