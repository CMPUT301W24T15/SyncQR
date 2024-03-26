package com.example.sync.organizer;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.Event;
import com.example.sync.R;

public class DeleteDialog extends DialogFragment {
    private DeleteDialogListener listener;
    interface DeleteDialogListener {
        public void notifyDeleteEvent();
    }

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

    public void setListener(DeleteDialogListener listener) {
        this.listener = listener;
    }
}

