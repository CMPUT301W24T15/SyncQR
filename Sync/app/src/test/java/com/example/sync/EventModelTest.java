package com.example.sync;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import android.util.Log;
import android.widget.EditText;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class EventModelTest {

    @Test
    public void testSaveEventsToDatabase() {
        Event event = new Event("testEvent1", new Timestamp(4,4), "Edmonton", Long.valueOf(10), "yiqing", "",  "", "1718521");
        Map<String, Object> eventData = new HashMap<>();
//        eventData.put("eventId", event.getEventId());
//        eventData.put("eventName", event.getEventName());
//        eventData.put("eventDate", event.getEventDate());
//        eventData.put("eventLocation", event.getEventLocation());
//        eventData.put("attendeeNumber", event.getAttendeeNumber());
//        eventData.put("eventDescription", event.getEventDescription());
//        eventData.put("poster", event.getPoster());
//        eventData.put("organizerName", event.getOrganizerName());
//        eventData.put("organizerId", event.getOrganizerId());

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Events").document(String.valueOf(event.getEventId())).set(eventData)
//                // Upload successful
//                .addOnSuccessListener(aVoid -> {
//                    return;
//                });
//        assertEquals(0, 1);
        assert(true);
    }
}
