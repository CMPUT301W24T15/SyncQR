package com.example.sync.organizer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sync.Checkin;
import com.example.sync.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for modifying event details and performing related operations such as deletion, notification, viewing attendee list, generating QR codes, and more.
 * It contains various buttons and functionalities related to event management.
 * It represents event is in its modified mode
 * This is a subclass of Fragment
 */
public class ModifyFrag extends Fragment {
    /**
     * Communicate to its parent and parent's parent to delete an event
     */
    interface ModifyListener {
        void notifyGrandparent();
    }

    /**
     * The listener that communicate with its parnet
     */
    private ModifyListener listener;
    /**
     * The view displayed checkin number
     */
    private TextView checkinNum;
    /**
     * The view displayed sign up number
     */
    private TextView signupNum;
    /**
     * The button for deletion
     */
    private ImageView delete;
    /**
     * The button for notification
     */
    private ImageView notify;
    /**
     * The button for generating promotion code
     */
    private ImageView promotion;
    /**
     * The button for generating checkin QR code
     */
    private ImageView generate;
    /**
     * The button for displaying all relevant lists
     */
    private ImageView viewList;
    /**
     * The mao that shows all user's location
     */
    private MapView map;
    /**
     * The specific event that is being operating
     */
    private Event event;
    /**
     * The fragment itself
     */
    private ModifyFrag self = this;

    /**
     * Creates a new instance of ModifyFrag with the given event.
     * @param event The event to be modified.
     * @return ModifyFrag
     */
    static ModifyFrag newInstance(Event event) {
        // create the fragment instance
        ModifyFrag fragment = new ModifyFrag();

        Bundle args = new Bundle();
        args.putSerializable("event", event);
        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Inflate a  layout
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.sync.R.layout.modify_organizer, container, false);
        return view;
    }

    /**
     * Link to all views that are related
     * Initialize the map
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @see Configuration
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        delete = view.findViewById(com.example.sync.R.id.delete_button);
        notify = view.findViewById(com.example.sync.R.id.notify_button);
        promotion = view.findViewById(com.example.sync.R.id.promotion_button);
        viewList = view.findViewById(com.example.sync.R.id.view_list_button);
        generate = view.findViewById(com.example.sync.R.id.qrcode_button);
        checkinNum = view.findViewById(com.example.sync.R.id.check_num);
        signupNum = view.findViewById(com.example.sync.R.id.sign_num);
        map = view.findViewById(com.example.sync.R.id.map);

        // Initialize osmdroid configuration
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));        // Set initial map center and zoom level

        // Set the tile source (e.g., Mapnik)
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setCenter(new GeoPoint(53.52342616882704, -113.52571167323268)); // Set initial center coordinates
        map.getController().setZoom(17.0f); // Set initial zoom level
    }


    /**
     * Called when the fragment is starting. Fetches the event data from the arguments bundle,
     * displays the users' locations on the map, and sets up listeners for various views.
     */
    @Override
    public void onStart() {
        super.onStart();

        // get event (not safe)
        Bundle args = getArguments();
        if (args != null) {
            event = (Event) args.getSerializable("event");
        }

        // display map
        Checkin.getLocationFromDatabase(event.getEventId(), new Checkin.Callback() {
            @Override
            public void onSuccess(ArrayList<LatLng> locations) {
                for (LatLng location : locations) {
                    Marker marker = new Marker(map);
                    marker.setPosition(new GeoPoint(location.latitude, location.longitude));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(marker);
                }
            }
        });

        // display statistics
        displayNumber(event.getEventId());

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
                NotifyDialog notifyDialog = NotifyDialog.newInstance(event);
                notifyDialog.show(getChildFragmentManager(), "Notification");
            }
        });


        // view list operation: display attendee list
        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkin.getListFromDatabase(event.getEventId(), new Checkin.Callback() {
                    @Override
                    public void onSuccess(String id, String name, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                        // convert map to arraylist
                        ArrayList<String> countsInList = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : counts.entrySet()) {
                            String key = entry.getKey();
                            String value = (String) entry.getValue();
                            String user = key + ": " + value;
                            countsInList.add(user);
                        }

                        ViewAttendeeDialog dialog = ViewAttendeeDialog.newInstance(countsInList, current, signup);
                        dialog.show(getChildFragmentManager(), "view list");
                    }
                });
            }
        });

        // generate action: reuse / generate QR code and share
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QrCodeDialog qrCodeDialog = QrCodeDialog.newInstance(event.getEventId());
                qrCodeDialog.show(getChildFragmentManager(), "qr code dialog");
            }
        });

        // generate a unique promotion QR code that links to the event description and event poster in the app.
        promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoDialog promoDialog = PromoDialog.newInstance(event.getEventId());
                promoDialog.show(getChildFragmentManager(), "promotion dialog");
            }
        });

    }

    /**
     * Set the listener
     * @param listener A listener that used to communicate with its parent
     */
    public void setListener(ModifyListener listener) {
        this.listener = listener;
    }

    /**
     * Displays the number of check-ins and sign-ups for the event.
     * @param eventId The ID of the event to display statistics for.
     */
    public void displayNumber(String eventId) {
        Checkin.getListFromDatabase(eventId, new Checkin.Callback() {
            @Override
            public void onSuccess(String id, String name, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                checkinNum.setText(String.valueOf(current.size()));
                signupNum.setText(String.valueOf(signup.size()));
            }
        });
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * Resumes the map rendering.
     */
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * Called when the Fragment is no longer resumed.
     * Pauses the map rendering.
     */
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
