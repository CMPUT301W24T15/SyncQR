package com.example.sync;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Event Model class to store related information and to be stored in database.
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
    private String organizerId;

    /**
     * Constructor for creating an event
     *
     * @param eventName name of the event
     * @param eventDate date of the event
     * @param eventLocation location of the event
     * @param attendeeNumber number of attendees that signed up
     * @param organizerName name of the organizer
     * @param eventDescription Description of the event
     * @param poster url of the poster image
     * @param organizerId userID of the organizer
     */
    public Event(String eventName, Timestamp eventDate, String eventLocation, Long attendeeNumber, String organizerName, String eventDescription, String poster, String organizerId) {

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

    public Event(String id, String eventName, Timestamp eventDate, String eventLocation, Long attendeeNumber, String organizerName, String eventDescription, String poster, String organizerId) {

        // eventID
        // is set to a string for convenience!!

        this.eventId = id;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.attendeeNumber = attendeeNumber;
        this.eventDescription = eventDescription;
        this.poster = poster;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
    }

    public Event() {
    }

    /**
     * Interface for callback function to be called when retrieving data from database
     */
    public interface Callback{
        default void onSuccess(Event event){}
        default void onSuccess(ArrayList<Event> eventArrayList) {}
        default void onSuccessReturnId(ArrayList<String> idList){}
    }

    /**
     * Static method to get a specific event from the database given eventID
     * @param eventID eventID of the target event
     * @param callback callback function to be used after finding the target event
     */
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
                                String organizerId = (String) data.get("organizerId");
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
    public static void getAllEventFromDatabase(Callback callback) {
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
                                String organizerId = (String) data.get("organizerId");
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

    /**
     * Delete a specific event from the database given eventID
     * @param eventId eventID of the event to delete
     */
    public static void deleteEvent(String eventId){
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
     * Retrieves the list of event IDs created by a specific user.
     *
     * @param userId   The user ID whose created event ID list is being retrieved.
     * @param callback The callback interface to handle the result of the asynchronous operation.
     *                 The result will be returned through the {@link Callback#onSuccessReturnId(ArrayList)}
     *                 method.
     */
    public static void getCreatedEventIdList(String userId, Callback callback) {
        db.collection("Accounts")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        // Obtain the id list of events created by this user
                        if (document.exists()) {
                            ArrayList<String> idList = (ArrayList<String>) document.getData().get("createdevents");
                            callback.onSuccessReturnId(idList);
                        }
                    }
                });
    }

    /**
     * Registers an event in the account of a user.
     *
     * @param userId  The user ID to register the event in.
     * @param eventId The ID of the event to register in the user's account.
     */
    public static void registerInAccount(String userId, String eventId) {
        db.collection("Accounts").whereEqualTo("userID", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                // Found the user document, now update it
                                DocumentReference userRef = document.getReference();
                                userRef.update("createdevents", FieldValue.arrayUnion(eventId))
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's created events successfully."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding event to user's created events.", e));
                            } else {
                                Log.d(TAG, "No such user document with given userId");
                            }
                        }
                    } else {
                        Log.d(TAG, "Error finding user document: ", task.getException());
                    }
                });
    }

    /**
     * Removes the poster of a specific event by setting its poster field to an empty string.
     *
     * @param eventId The ID of the event whose poster is to be removed.
     */
    public static void removePoster(String eventId) {
        DocumentReference doc = db.collection("Events").document(eventId);
        Map<String, Object> data = new HashMap<>();
        data.put("poster", "");

        doc.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Poster removed successfully");
                    }
                });
    }

    /**
     * Getters and Setters
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

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

}
