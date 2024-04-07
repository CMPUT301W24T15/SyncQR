package com.example.sync.organizer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.sync.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ModifyFrag extends Fragment {
    interface ModifyListener {
        void notifyGrandparent();
    }
    private ModifyListener listener;
    private TextView checkinNum;
    private TextView signupNum;
    private ImageView delete;
    private ImageView notify;
    private ImageView promotion;
    private ImageView generate;
    private ImageView viewList;
    private MapView map;
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
        promotion = view.findViewById(R.id.promotion_button);
        viewList = view.findViewById(R.id.view_list_button);
        generate = view.findViewById(R.id.qrcode_button);
        checkinNum = view.findViewById(R.id.check_num);
        signupNum = view.findViewById(R.id.sign_num);
        map = view.findViewById(R.id.map);
        map.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // get event (not safe)
        Bundle args = getArguments();
        if (args != null) {
            event = (Event) args.getSerializable("event");
        }

        // display map

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //acquire location list from database'
                Checkin.getLocationFromDatabase(event.getEventId(), new Checkin.Callback() {
                    @Override
                    public void onSuccess(ArrayList<LatLng> locations) {
                        for (LatLng location: locations){
                            googleMap.addMarker(new MarkerOptions().position(location));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 3.0f));
                        }
                    }
                });
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
                    public void onSuccess(String name, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
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
                QrCodeDialog qrcodeDialog = QrCodeDialog.newInstance(event.getEventId());
                qrcodeDialog.show(getChildFragmentManager(), "qr code dialog");
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

    public void setListener(ModifyListener listener) {
        this.listener = listener;
    }

    public void displayNumber(String eventId) {
        Checkin.getListFromDatabase(eventId, new Checkin.Callback() {
            @Override
            public void onSuccess(String name, Map<String, Object> counts, ArrayList<String> current, ArrayList<String> signup) {
                checkinNum.setText(String.valueOf(current.size()));
                signupNum.setText(String.valueOf(signup.size()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

}
