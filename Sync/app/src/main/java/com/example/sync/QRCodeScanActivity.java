package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
/**
 * This is a class that can scan QR code
 */
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScanActivity extends AppCompatActivity {

    // Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        // Initialize the QR Code scanner on activity start
        new IntentIntegrator(this).initiateScan();
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Handle scan failure
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Handle scan success
                processScannedData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processScannedData(String scannedData) {
        if (scannedData.startsWith("CHECKIN:")) {
            // Extract check-in information and upload to Firestore
            //uploadCheckInInfo(scannedData.substring("CHECKIN:".length()));
            int i = 1;
        } else if (scannedData.startsWith("EVENTID:")) {
            // Assume it's a URL to an event page and start EventActivity
            Intent intent = new Intent(this, Event.class);
            intent.putExtra("EVENTID", scannedData);
            startActivity(intent);
        } else {
            // Handle other types of data, if necessary
            Toast.makeText(this, "Scanned: " + scannedData, Toast.LENGTH_LONG).show();
        }
    }

//    private void uploadCheckInInfo(String checkInInfo) {
//        // Assuming checkInInfo contains the necessary information for check-in
//        // Adjust the database path and document structure as needed
//        db.collection("Checked-in System")
//                .add(/* Your check-in data model here, e.g., new CheckIn(checkInInfo) */)
//                .addOnSuccessListener(documentReference -> Toast.makeText(QRCodeScanActivity.this, "Check-in successful", Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e -> Toast.makeText(QRCodeScanActivity.this, "Check-in failed", Toast.LENGTH_SHORT).show());
//    }
}
