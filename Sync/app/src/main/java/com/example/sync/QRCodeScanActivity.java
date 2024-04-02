package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sync.Close.EventDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Activity to handle QR code scanning and processing the scanned data.
 * It can initiate actions based on the scanned data, such as navigating to event details
 * or processing check-in information.
 */
public class QRCodeScanActivity extends AppCompatActivity {

    // Firestore instance
    private FirebaseFirestore db;

    /**
     * Initializes the activity, setting up Firestore and starting the QR code scanner.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        // Initialize the QR Code scanner on activity start
        new IntentIntegrator(this).initiateScan();
    }

    /**
     * Handles the result of the QR code scan.
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                processScannedData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Processes the scanned QR code data.
     * Can handle different types of data encoded in QR codes, such as event IDs or check-in information.
     * @param scannedData The raw data obtained from scanning a QR code.
     */
    private void processScannedData(String scannedData) {
        if (scannedData.startsWith("CHECKIN:")) {
            // Handle check-in logic here
        } else if (scannedData.startsWith("EVENTID:")) {
            // Extract the event ID from the scanned data
            String eventId = scannedData.substring("EVENTID:".length());
            // Start EventDetailsActivity with the extracted event ID
            EventDetailsActivity.startWithEventId(this, eventId);
        } else {
            Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();
        }
    }
}
