package com.example.sync;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a class that keeps the methods of event
 */
public class Event {

    private String eventId;
    private String eventName;
    private Date eventDate;
    private String eventLocation;
    private int attendeeNumber;
    private String eventDescription;
    private String poster;
    private String organizerId;
    private Map<String, Boolean> attendees;

    private transient FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Event(String eventId, String eventName, Date eventDate, String eventLocation,
                 int attendeeNumber, String eventDescription, String poster, String organizerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.attendeeNumber = attendeeNumber;
        this.eventDescription = eventDescription;
        this.poster = poster;
        this.organizerId = organizerId;
        this.attendees = new HashMap<>();
    }

    /**
     * This is a method that save the event information into database
     */
    public void saveEventToDatabase() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDate", eventDate);
        eventData.put("eventLocation", eventLocation);
        eventData.put("attendeeNumber", attendeeNumber);
        eventData.put("eventDescription", eventDescription);
        eventData.put("poster", poster);
        eventData.put("organizerId", organizerId);
        eventData.put("attendees", attendees);

        db.collection("events").document(eventId).set(eventData)
                // Upload successful
                .addOnSuccessListener(aVoid -> {
                })
                // If error occurs during upload
                .addOnFailureListener(e -> {
                });
    }
    /**
     * This is a class that keeps track of check in attendance
     */
    public void checkInAttendee(String attendeeId) {
        attendees.put(attendeeId, true);
        // Recalculate the number of attendees
        attendeeNumber = attendees.size();
        // Save the updated attendee list and count
        saveEventToDatabase();
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

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getAttendeeNumber() {
        return attendeeNumber;
    }

    public void setAttendeeNumber(int attendeeNumber) {
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

    public Map<String, Boolean> getAttendees() {
        return attendees;
    }

    public void setAttendees(Map<String, Boolean> attendees) {
        this.attendees = attendees;
        // Update attendee count
        this.attendeeNumber = attendees.size();
    }
}
