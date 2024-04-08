package com.example.sync.organizer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.Checkin;
import com.example.sync.Event;
import com.example.sync.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A DialogFragment for displaying attendee details in an ExpandableListView.
 */
public class ViewAttendeeDialog extends DialogFragment {
    /**
     * The expandable list view used to display three lists
     */
    ExpandableListView attendeeList;

    /**
     * Creates a new instance of ViewAttendeeDialog.
     * @param counts The list of check in counts.
     * @param current The list of current attendees.
     * @param signup The list of sign-up attendees.
     * @return A new instance of ViewAttendeeDialog.
     */
    static ViewAttendeeDialog newInstance(ArrayList<String> counts, ArrayList<String> current, ArrayList<String> signup) {
        // create the fragment instance
        ViewAttendeeDialog dialog = new ViewAttendeeDialog();

        Bundle args = new Bundle();
        args.putSerializable("counts", counts);
        args.putSerializable("current", current);
        args.putSerializable("signup", signup);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Create a new dialog
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return AlertDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_attendee, null);
        attendeeList = view.findViewById(R.id.view_attendee);

        // Set the title
        builder.setTitle("Tap To See Detail");
        builder.setView(view);


        Bundle args = getArguments();
        if (args != null) {

            // Prepare parent list
            ArrayList<String> name = new ArrayList<String>();
            name.add("Sign Up Attendees");
            name.add("Checkin Attendees (Current)");
            name.add("Checkin Attendees (Counts)");

            // Prepare child list
            ArrayList<String> counts = (ArrayList<String>) args.getSerializable("counts");
            ArrayList<String> current = (ArrayList<String>) args.getSerializable("current");
            ArrayList<String> signup = (ArrayList<String>) args.getSerializable("signup");

            HashMap<String, ArrayList<String>> content = new HashMap<>();
            content.put("Sign Up Attendees", signup);
            content.put("Checkin Attendees (Current)", current);
            content.put("Checkin Attendees (Counts)", counts);

            ViewAttendeeAdapter adapter = new ViewAttendeeAdapter(getContext(), name, content);
            attendeeList.setAdapter(adapter);
        }
        return builder.create();
    }
}
