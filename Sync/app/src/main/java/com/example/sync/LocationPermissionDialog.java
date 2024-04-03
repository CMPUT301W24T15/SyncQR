package com.example.sync;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog fragment for requesting location permission.
 */
public class LocationPermissionDialog extends DialogFragment {

    /**
     * Interface for listening to location permission requests.
     */
    public interface LocationPermissionDialogListener {
        /**
         * Callback method invoked when location permission is requested.
         */
        void onRequestLocationPermission();
    }

    private LocationPermissionDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LocationPermissionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LocationPermissionDialogListener");
        }
    }

    /**
     * Creates the dialog for requesting location permission.
     *
     * @param savedInstanceState A Bundle containing the saved state.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Location Permission")
                .setMessage("This app requires location access to provide personalized event recommendations. Do you want to enable location permissions?")
                .setPositiveButton("Enable", (dialog, which) -> {
                    listener.onRequestLocationPermission();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
        return builder.create();
    }
}


