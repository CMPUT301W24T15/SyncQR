package com.example.sync;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import android.util.Log;
import android.widget.EditText;
import android.content.Context;

import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


//@RunWith(MockitoJUnitRunner.class)
public class EventModelTest {

//    @Mock
//    private Context context;

    public void testSaveEventsToDatabase() {
//        when(context.getString(R.string.app_name)).thenReturn("Sync");
//        when(context.getString(R.string.homeBtn)).thenReturn("Home");
//        when(context.getString(R.string.eventBtn)).thenReturn("Event");
//        when(context.getString(R.string.profileBtn)).thenReturn("Profile");
//        when(context.getString(R.string.messagesBtn)).thenReturn("Messages");
//        when(context.getString(R.string.profile)).thenReturn("");
//        when(context.getString(R.string.event)).thenReturn("");
//        when(context.getString(R.string.messages)).thenReturn("");
//        when(context.getString(R.string.event_poster)).thenReturn("");
//        when(context.getString(R.string.home)).thenReturn("");
//        when(context.getString(R.string.welcome_image_desc)).thenReturn("");
//        when(context.getString(R.string.event_image_desc)).thenReturn("");
//        when(context.getString(R.string.event_details)).thenReturn("");
//        when(context.getString(R.string.event_description_default)).thenReturn("\n");
//        when(context.getString(R.string.default_notification_channel_id)).thenReturn("default_channel_id");
//        when(context.getString(R.string.channel_name)).thenReturn("Notification Channel");
//        when(context.getString(R.string.channel_description)).thenReturn("Channel to receive Notification");
        Event event = new Event("testEvent1", new Timestamp(4,4), "Edmonton", Long.valueOf(10), "yiqing", "",  "", "1718521");
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", event.getEventId());
        eventData.put("eventName", event.getEventName());
        eventData.put("eventDate", event.getEventDate());
        eventData.put("eventLocation", event.getEventLocation());
        eventData.put("attendeeNumber", event.getAttendeeNumber());
        eventData.put("eventDescription", event.getEventDescription());
        eventData.put("poster", event.getPoster());
        eventData.put("organizerName", event.getOrganizerName());
        eventData.put("organizerId", event.getOrganizerId());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(String.valueOf(event.getEventId())).set(eventData)
                // Upload successful
                .addOnSuccessListener(aVoid -> {
                    assert(true);
                    return;
                });
        assert(false);
    }
}
