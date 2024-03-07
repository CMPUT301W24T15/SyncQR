package com.example.sync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the QR Code scanner on activity start
        new IntentIntegrator(this).initiateScan();
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                // Handle scan failure
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Handle scan success
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // Proceed with check-in process, e.g., sending scanned data to server
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
