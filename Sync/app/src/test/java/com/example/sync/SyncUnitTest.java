package com.example.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

//    @Test
//    public void testSaveProfileData() {
//        // Set up test data
//        String name = "John Doe";
//        String homepage = "https://example.com";
//        String contact = "john@example.com";
//
//        // Mock EditText input
//        when(userNameInput.getText().toString()).thenReturn(name);
//        when(userHomepageInput.getText().toString()).thenReturn(homepage);
//        when(userContactInput.getText().toString()).thenReturn(contact);
//
//        // Call the method under test
//        Profile profile = new Profile(databaseReference);
//        profile.saveProfileData();
//
//        // Verify that setValue() method is called with correct arguments
//        verify(databaseReference, times(1)).push().getKey();
//        verify(databaseReference, times(1)).child(anyString());
//        verify(databaseReference, times(1)).setValue(any(Profile.class));
//    }

    /**
     * Instrumented test for the CodeGenerator class.
     */
//    @Test
//    public void CodeGeneratorTest() {
//
//        // Context of the app under test.
//        String testText = "Test QR Code";
//        int width = 500;
//        int height = 500;
//
//        Bitmap resultBitmap = QRCodeGenerator.generateQRCodeBitmap(testText, width, height);
//
//
//        assertNotNull("The generated QR Code bitmap should not be null", resultBitmap);
//        assertEquals("The width of the generated bitmap is not as expected", width, resultBitmap.getWidth());
//        assertEquals("The height of the generated bitmap is not as expected", height, resultBitmap.getHeight());
//    }
//
//    @Test
//    public void testEventListActivity() {
//
//    }

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