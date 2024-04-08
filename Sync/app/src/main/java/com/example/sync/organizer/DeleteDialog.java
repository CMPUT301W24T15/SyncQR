package com.example.sync.organizer;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog to be displayed when an organizer tries deleting an event.
 * @see DialogFragment
 */
public class DeleteDialog extends DialogFragment {
    /**
     * A listener use to notify its parent
     */
    private DeleteDialogListener listener;

    /**
     * The procedure that should be executed when the user wants to delete the event
     */
    interface DeleteDialogListener {
        public void notifyDeleteEvent();
    }

    /**
     * The process of dialog deletion
     * Basically just confirm the choice of the user
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return AlertDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(com.example.sync.R.layout.delete_event, null);

        // set the dialog
        builder.setView(view)
                .setTitle("Delete")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // inform the list to delete the event
                    listener.notifyDeleteEvent();
                });

        return builder.create();
    }

    /**
     * Set the listener for the fragment
     * @param listener
     */
    public void setListener(DeleteDialogListener listener) {
        this.listener = listener;
    }
}

