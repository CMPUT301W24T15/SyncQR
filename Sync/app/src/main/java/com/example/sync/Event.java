package com.example.sync;

import android.media.Image;

import java.util.ArrayList;

public class Event {

    private Image poster;

    private Image QRCode;

    private ArrayList<Attendee> attendees;   // all the attendees of this event

    public Event() {

    }

    public void CheckIn(Attendee attendee) {
        /*
        this is just pseudocode, can be changed according to the function of databse.
        if (database doesn't have this attendee) {
            this.attendees.add(attendee);
            database.addEntry(attendee);
        }
        else {
            database.countOfAttendee += 1;
        }
        */
    }

    public void setPoster(Image poster) {
        this.poster = poster;
    }

    public Image getPoster() {
        return poster;
    }

    public Image getQRCode() {
        return QRCode;
    }

    public void setQRCode(Image QRCode) {
        this.QRCode = QRCode;
    }

    public ArrayList<Attendee> getAttendees() {
        return attendees;
    }

}
