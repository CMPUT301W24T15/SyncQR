package com.example.sync.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.sync.Event;
import com.google.android.material.navigation.NavigationBarView;

/**
 * A fragment that contains two sub-fragments, can transfer from one to the other one
 * Achieved this by using a navigation bar
 * The two sub-fragments are DisplayFrag and ModifyFrag
 * This is a subclass of Fragment
 * @see Fragment
 * @see DisplayFrag
 * @see ModifyFrag
 */
public class EventDetailFrag extends Fragment {

    /**
     * A listener that use to notify its parent fragment
     */
    interface EventDetailListener {
        /**
         * A listener that use to notify its parent fragment
         * @param event The event instance that needs to be deleted
         */
        void notifyParent(Event event);
    }

    /**
     * A listener that use to notify its parent fragment
     */
    private EventDetailListener listener;
    /**
     * The back button
     */
    private Toolbar toolbar;
    /**
     * The navigation bar that navigates the user to DisplayFrag and ModifyFrag
     */
    private NavigationBarView navigationBar;
    /**
     * The instance itself
     */
    private EventDetailFrag self = this;

    /**
     * Create a new instance of the fragment with an argument
     * Call its constructor within the static method
     * @param event The correspondent event instance
     * @return EventDetailFrag
     */
    static EventDetailFrag newInstance(Event event) {
        // create the fragment instance
        EventDetailFrag fragment = new EventDetailFrag();
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Inflate a layout
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.sync.R.layout.event_detail_organizer, container, false);
        return view;
    }

    /**
     * Link all views
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(com.example.sync.R.id.toolbar);
        navigationBar = view.findViewById(com.example.sync.R.id.navigation_bar);
    }

    /**
     * Determine the procedure after the fragment has been created'
     * Unbindle the argument
     * navigate to different fragments according to user's operation
     */
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
                .replace(com.example.sync.R.id.overview_modify_container, DisplayFrag.newInstance(event))
                .commit();

        // when click on the navigation bar
        navigationBar.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
            if (item.getItemId() == com.example.sync.R.id.modify) {
                ModifyFrag modifyFrag =  ModifyFrag.newInstance(event);
                modifyFrag.setListener(new ModifyFrag.ModifyListener() {
                    @Override
                    public void notifyGrandparent() {
                        listener.notifyParent(event);
                    }
                });

                getChildFragmentManager().beginTransaction()
                        .replace(com.example.sync.R.id.overview_modify_container, modifyFrag)
                        .commit();
            } else {
                getChildFragmentManager().beginTransaction()
                        .replace(com.example.sync.R.id.overview_modify_container, DisplayFrag.newInstance(event))
                        .commit();
            } return true;
        });
    }

    /**
     * Set the listener
     * @param listener The listener that communicate with fragment's parent
     */
    public void setListener(EventDetailListener listener) {
        this.listener = listener;
    }
}