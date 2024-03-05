package com.example.sync;

import java.util.Date;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private String description;
    private String location;
    private Date eventDate;
    private String qrCodeUrl;
    private List<String> attendeeIds;

    // Constructor
    public Event(String id, String name, String description, String location, Date eventDate, String qrCodeUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.qrCodeUrl = qrCodeUrl;
        this.attendeeIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public List<String> getAttendeeIds() {
        return attendeeIds;
    }

    public void setAttendeeIds(List<String> attendeeIds) {
        this.attendeeIds = attendeeIds;
    }

    // Methods to manage attendees
    public void addAttendee(String attendeeId) {
        if (!attendeeIds.contains(attendeeId)) {
            attendeeIds.add(attendeeId);
        }
    }

    public void removeAttendee(String attendeeId) {
        attendeeIds.remove(attendeeId);
    }
}
