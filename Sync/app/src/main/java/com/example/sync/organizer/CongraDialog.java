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

public class CongraDialog extends DialogFragment {
    static CongraDialog newInstance(String name, String count) {
        // create the fragment instance
        CongraDialog dialog = new CongraDialog();

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("count", count);
        dialog.setArguments(args);

        return dialog;
    }

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
