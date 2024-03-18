package com.example.sync;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Map;

public class SyncUnitTest {
    @Mock
    private EditText userNameInput;

    @Mock
    private EditText userHomepageInput;

    @Mock
    private EditText userContactInput;

    @Mock
    private DatabaseReference databaseReference;

    @Before
    public void setUp() {
        // Initialize mock objects
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveProfileData() {
        // Set up test data
        String name = "John Doe";
        String homepage = "https://example.com";
        String contact = "john@example.com";

        // Mock EditText input
        when(userNameInput.getText().toString()).thenReturn(name);
        when(userHomepageInput.getText().toString()).thenReturn(homepage);
        when(userContactInput.getText().toString()).thenReturn(contact);

        // Call the method under test
        Profile profile = new Profile(databaseReference);
        profile.saveProfileData();

        // Verify that setValue() method is called with correct arguments
        verify(databaseReference, times(1)).push().getKey();
        verify(databaseReference, times(1)).child(anyString());
        verify(databaseReference, times(1)).setValue(any(ProfileP.class));
    }

    @Test
    public void testEventListActivity() {

    }

//    @Test
//    public void testGetEventName() {
//        String name = "testGetEventName";
//        Event event = new Event(name, new Date(100000009), "Edmonton", "Kevin", "Description", "sample poster", 10000009);
//        Log.d("Test", event.getEventName());
//        assertEquals(event.getEventName(), name);
//    }
//
//    @Test
//    public void testGetAttendees() {
//        String name = "testGetEventName";
//        Event event = new Event(name, new Date(100000009), "Edmonton", "Kevin", "Description", "sample poster", 10000009);
//        Map<String, Boolean> attendee = event.getAttendees();
//        assert(attendee.size() == 0);
//    }

}