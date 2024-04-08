package com.example.sync.organizer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.R;

/**
 * Shows a congratulation dialog when any of created events reached the milestone (10, 20, 40, 80)
 * The dialog will display the correspondent event name and its current number of check in
 * This is a subclass of DialogFragment
 * @see DialogFragment
 */
public class CongraDialog extends DialogFragment {

    /**
     * Create a new instance of the dialog with arguments
     * Call its constructor within the static method
     * @param name The name of the event that reaches the milestone
     * @param count The current number of people who checked in this event
     * @return CongraDialog
     */
    static CongraDialog newInstance(String name, String count) {
        // create the fragment instance
        CongraDialog dialog = new CongraDialog();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("count", count);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Set the title of the dialog as "Congratualtions"
     * Link and find all relevant views from xml files
     * Unbundle all the arguments, "name" for event name and "count" for displayed number
     * Set text and display them
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return AlertDialog (has been created)
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Congratulations!");

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.congratulation, null);
        builder.setView(view);

        TextView eventName = view.findViewById(R.id.event_name);
        TextView congratulation = view.findViewById(R.id.congratulation);

        // get event information
        Bundle args = getArguments();
        String name = args.getString("name");
        String count = args.getString("count");

        // display
        eventName.setText(name);
        String text = count + " people have already checked in!";
        congratulation.setText(text);


        return builder.create();
    }
}
