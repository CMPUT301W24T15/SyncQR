package com.example.sync.Open;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sync.EventIDGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a class that keeps the methods of event
 */
public class Event implements Serializable {

    private static String TAG = "Event";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;
    private String eventName;
    private Timestamp eventDate;
    private String eventLocation;
    private Long attendeeNumber;
    private String organizerName;
    private String eventDescription;
    private String poster;
    private Long organizerId;

    // Empty constructor required by Firestore
    public Event() {
        // Default constructor required for Firestore
    }

    public Event(String eventName, Timestamp eventDate, String eventLocation, Long attendeeNumber, String organizerName, String eventDescription, String poster, Long organizerId) {

        // eventID
        // is set to a string for convenience!!

        this.eventId = Integer.toString(EventIDGenerator.generate());
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.attendeeNumber = attendeeNumber;
        this.eventDescription = eventDescription;
        this.poster = poster;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
    }

    public interface Callback{
        default void onSuccess(Event event) {
        }

        default void onSuccess(ArrayList<Event> eventArrayList) {
        }
    }


    public static void getEventFromDatabase(String eventID, Callback callback){
        db.collection("Events")
                .whereEqualTo("eventId", eventID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // check if the query is completed
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // record log
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Obtain all fields
                                Map<String, Object> data = document.getData();
                                String name = (String) data.get("eventName");
                                Timestamp date = (Timestamp) data.get("eventDate");
                                String location = (String) data.get("eventLocation") ;
                                Long limit = (Long) data.get("attendeeNumber");
                                String organizerName = (String) data.get("organizerName");
                                Long organizerId = (Long) data.get("organizerId");
                                String eventDescription = (String) data.get("eventDescription");
                                String poster = (String) data.get("poster");

                                // Create a new event instance, make sure to manually assigned the eventID
                                // so that it restores the original event object, or creates a new one otherwise
                                Event event = new Event(name, date, location, limit, organizerName, eventDescription, poster, organizerId);
                                event.setEventId(eventID);

                                // notify finished
                                callback.onSuccess(event);
                            }
                        }
                    }
                });
    }

    /***
     * This method return all events after current time
     * @param callback when event array is ready, it return the event array
     */
    public static void getAllEventFromDatabase(Callback callback){
        Timestamp current = new Timestamp(new Date());
        db.collection("Events")
                .whereGreaterThanOrEqualTo("eventDate", current)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // check if the query is completed
                        if (task.isSuccessful()){
                            ArrayList<Event> eventArrayList = new ArrayList<Event>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // record log
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                // Obtain all fields
                                Map<String, Object> data = document.getData();
                                String id = (String) data.get("eventId");
                                String name = (String) data.get("eventName");
                                Timestamp date = (Timestamp) data.get("eventDate");
                                String location = (String) data.get("eventLocation") ;
                                Long limit = (Long) data.get("attendeeNumber");
                                String organizerName = (String) data.get("organizerName");
                                Long organizerId = (Long) data.get("organizerId");
                                String eventDescription = (String) data.get("eventDescription");
                                String poster = (String) data.get("poster");

                                // Create a new event instance, make sure to manually assigned the eventID
                                // so that it restores the original event object, or creates a new one otherwise
                                Event event = new Event(name, date, location, limit, organizerName, eventDescription, poster, organizerId);
                                event.setEventId(id);

                                // add to list
                                eventArrayList.add(event);
                            }

                            // notify finished
                            callback.onSuccess(eventArrayList);
                        }
                    }
                });
    }


    /**
     * This is a method that save the event information into database
     */

    public void saveEventToDatabase() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("eventName", eventName);
        eventData.put("eventDate", eventDate);
        eventData.put("eventLocation", eventLocation);
        eventData.put("attendeeNumber", attendeeNumber);
        eventData.put("eventDescription", eventDescription);
        eventData.put("poster", poster);
        eventData.put("organizerName", organizerName);
        eventData.put("organizerId", organizerId);


        db.collection("Events").document(String.valueOf(eventId)).set(eventData)
                // Upload successful
                .addOnSuccessListener(aVoid -> {
                })
                // If error occurs during upload
                .addOnFailureListener(e -> {
                });
    }

    public void deleteEvent(String eventId){
        DocumentReference doc = db.collection("Events").document(eventId);
        doc.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }


    /**
     * The following methods are the setters and getters
     */
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Long getAttendeeNumber() {
        return attendeeNumber;
    }

    public void setAttendeeNumber(Long attendeeNumber) {
        this.attendeeNumber = attendeeNumber;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

}
