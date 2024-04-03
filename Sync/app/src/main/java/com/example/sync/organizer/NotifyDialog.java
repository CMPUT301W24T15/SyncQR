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
import com.example.sync.R;
import com.google.android.material.textfield.TextInputEditText;

public class NotifyDialog extends DialogFragment {

    static NotifyDialog newInstance(Event event) {
        // create the fragment instance
        NotifyDialog dialog = new NotifyDialog();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        dialog.setArguments(args);

        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.notify_organizer, null);

        // set the dialog
        builder.setView(view)
                .setTitle("Push Notifications")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Push", (dialog, which) -> {
                    TextInputEditText input = view.findViewById(R.id.message);
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