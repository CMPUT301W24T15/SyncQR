package com.example.sync.organizer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.Event;
import com.example.sync.Notification;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A dialog fragment used to push notifications for a specific event to attendees.
 * Allows organizers to input a message and push it as a notification to event attendees.
 */
public class NotifyDialog extends DialogFragment {

    /**
     * Creates a new instance of NotifyDialog with the provided event.
     * @param event The event for which notifications will be pushed.
     * @return NotifyDialog
     */
    static NotifyDialog newInstance(Event event) {
        // create the fragment instance
        NotifyDialog dialog = new NotifyDialog();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Called to create the dialog shown by this fragment.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(com.example.sync.R.layout.notify_organizer, null);

        // set the dialog
        builder.setView(view)
                .setTitle("Push Notifications")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Push", (dialog, which) -> {
                    TextInputEditText input = view.findViewById(com.example.sync.R.id.message);
                    String message = input.getText().toString();

                    // if the message is empty, reminds the user that this is an invalid input
                    if (message.isEmpty()){
                        Toast.makeText(getContext(), "Invalid Input!", Toast.LENGTH_LONG).show();
                    } else {
                        // push the notification
                        Bundle args = getArguments();
                        Event event = (Event) args.getSerializable("event");
                        String eventID = event.getEventId();
                        String title = event.getEventName();
                        Notification notification = new Notification(eventID, title, message);
                        notification.setNotification();
                        Toast.makeText(getContext(), "Notification Successfully Pushed!", Toast.LENGTH_LONG).show();

                    }
                });

        return builder.create();
    }
}