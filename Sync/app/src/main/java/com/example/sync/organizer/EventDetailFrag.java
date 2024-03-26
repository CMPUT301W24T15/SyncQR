package com.example.sync.organizer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.sync.Event;
import com.example.sync.R;
import com.google.android.material.navigation.NavigationBarView;

public class EventDetailFrag extends Fragment {
    interface EventDetailListener {
        void notifyParent(Event event);
    }
    private EventDetailListener listener;
    private Toolbar toolbar;
    private NavigationBarView navigationBar;
    private EventDetailFrag self = this;
    static EventDetailFrag newInstance(Event event) {
        // create the fragment instance
        EventDetailFrag fragment = new EventDetailFrag();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_detail_organizer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        navigationBar = view.findViewById(R.id.navigation_bar);
    }

    @Override
    public void onStart() {
        super.onStart();

        // back to view my events
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // default fragment
        Bundle args = self.getArguments();
        Event event = (Event) args.getSerializable("event");

        getChildFragmentManager().beginTransaction()
                .replace(R.id.overview_modify_container, DisplayFrag.newInstance(event))
                .commit();

        // when click on the navigation bar
        navigationBar.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
            if (item.getItemId() == R.id.modify) {
                ModifyFrag modifyFrag =  ModifyFrag.newInstance(event);
                modifyFrag.setListener(new ModifyFrag.ModifyListener() {
                    @Override
                    public void notifyGrandparent() {
                        listener.notifyParent(event);
                    }
                });

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.overview_modify_container, modifyFrag)
                        .commit();
            } else {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.overview_modify_container, DisplayFrag.newInstance(event))
                        .commit();
            } return true;
        });
    }

    public void setListener(EventDetailListener listener) {
        this.listener = listener;
    }
}