package com.example.sync.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sync.Open.Event;
import com.example.sync.R;


public class ModifyFrag extends Fragment {
    interface ModifyListener {
        void notifyGrandparent();
    }
    private ModifyListener listener;
    private ImageView delete;
    private ImageView notify;
    private Event event;
    private ModifyFrag self = this;

    static ModifyFrag newInstance(Event event) {
        // create the fragment instance
        ModifyFrag fragment = new ModifyFrag();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify_organizer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        delete = view.findViewById(R.id.delete_button);
        notify = view.findViewById(R.id.notify_button);
    }

    @Override
    public void onStart() {
        super.onStart();

        // delete operation: back to ViewEvent
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.setListener(new DeleteDialog.DeleteDialogListener() {
                    @Override
                    public void notifyDeleteEvent() {
                        listener.notifyGrandparent();
                    }
                });
                deleteDialog.show(getChildFragmentManager(), "Delete Event");

            }
        });

        // notify operation: send notification
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                if (args != null) {
                    Event event = (Event) args.getSerializable("event");
                    NotifyDialog notifyDialog = NotifyDialog.newInstance(event);
                    notifyDialog.show(getChildFragmentManager(), "Notification");
                }
            }
        });

    }


    public void setListener(ModifyListener listener) {
        this.listener = listener;
    }
}
