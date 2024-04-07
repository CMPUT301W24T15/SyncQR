package com.example.sync;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sync.organizer.OrganizerDashboard;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.GeoPoint;

import java.io.InputStream;

/**
 * Activity to handle QR code scanning and processing the scanned data.
 * It can initiate actions based on the scanned data, such as navigating to event details
 * or processing check-in information.
 */
public class QRCodeScanActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1031;
    private FusedLocationProviderClient fusedLocationClient;

    public static String eventId;


    /**
     * Initializes the activity, setting up Firestore and starting the QR code scanner.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ask location permission
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Show an option dialog for user to choose between camera scan or gallery pick
        showScanOptionDialog();
    }

    /**
     * Displays a dialog to choose between scanning a QR code using the camera or picking an image from the gallery.
     */
    private void showScanOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan QR Code")
                .setMessage("Choose your scan option")
                .setPositiveButton("Camera", (dialog, which) -> {
                    // Start QR code scanner
                    new IntentIntegrator(QRCodeScanActivity.this).initiateScan();
                })
                .setNegativeButton("Album", (dialog, which) -> {
                    // Start an activity to pick an image from the album
                    Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
                    pickImageIntent.setType("image/*");
                    startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            // Handle picking an image from the gallery result
            handleImagePickResult(resultCode, data);
        } else {
            // This will handle the QR code scan result from the camera
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled QR code scan", Toast.LENGTH_LONG).show();
                } else {
                    processScannedData(result.getContents());
                }
            }
        }
    }

    /**
     * Handles the result of picking an image from the gallery.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller.
     */
    private void handleImagePickResult(int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            processPickedQRCode(imageUri);
            Toast.makeText(this, "Image picked: " + imageUri.toString(), Toast.LENGTH_LONG).show();
            // processPickedQRCode(imageUri); // Uncomment this when method is implemented
        } else {
            Toast.makeText(this, "Image picking cancelled", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no image is picked
        }
    }

    /**
     * Processes the data obtained from scanning a QR code.
     * @param scannedData The data obtained from scanning the QR code.
     */
    private void processScannedData(String scannedData) {
        Toast.makeText(this, "Scanned QR Code: " + scannedData, Toast.LENGTH_LONG).show();

        if ("Administrator".equals(scannedData)) {
            // Handle Administrator action
            Intent intent = new Intent(QRCodeScanActivity.this, AdministratorDashboard.class);
            startActivity(intent);
        } else if (scannedData.startsWith("event")) {
            // Extract the event ID from the scanned data
            String eventId = extractSubstringAfterDelimiter(scannedData, ":"); // This skips the "event:" part
            // Start EventDetailsActivity with the extracted event ID
            EventDetailsActivity.startWithEventId(this, eventId);
        } else if (scannedData.startsWith("checkin")) {
            // Extract the event ID from the scanned data
            eventId = extractSubstringAfterDelimiter(scannedData, ":"); // This skips the "checkin:" part
            String userID = getIntent().getStringExtra("userID");
            Log.e("UserID:", "UserID = " + userID);
            fetchLocationAndCheckIn(userID);
        } else {
            // Handle any other unexpected QR code data
            Toast.makeText(this, "Unrecognized QR Code", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Processes the picked QR code image.
     * @param imageUri The URI of the picked QR code image.
     */
    private void processPickedQRCode(Uri imageUri) {
        try {
            // Convert URI to Bitmap
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            // Convert Bitmap to BinaryBitmap
            int[] intArray = new int[bitmap.getWidth()*bitmap.getHeight()];
            //copy pixel data from the Bitmap into the 'intArray' array
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            LuminanceSource source = new com.google.zxing.RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Try to decode the QR code
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            String qrCodeText = qrCodeResult.getText();
            processScannedData(qrCodeText);

            // Use the QR code text
            Toast.makeText(this, "QR Code Found: " + qrCodeText, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error decoding QR Code", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Extracts the substring after the specified delimiter.
     * @param input The input string.
     * @param delimiter The delimiter to search for.
     * @return The substring after the delimiter, or an empty string if the delimiter is not found.
     */
    private String extractSubstringAfterDelimiter(String input, String delimiter) {
        if (input == null || delimiter == null) {
            return ""; // Return empty string if input or delimiter is null
        }
        int delimiterIndex = input.indexOf(delimiter);
        if (delimiterIndex != -1 && delimiterIndex < input.length() - 1) {
            // If delimiter is found and it's not at the end of the string
            // Log the extracted substring
            Log.d("Substring", "Extracted substring after delimiter: " + input.substring(delimiterIndex + 1));
            return input.substring(delimiterIndex + 1); // Extract substring after the delimiter
        } else {
            return ""; // Return empty string if delimiter is not found or it's at the end of the string
        }
    }

    private void fetchLocationAndCheckIn(String userID) {
        checkLocationPermission();
        Checkin.checkInForUser(eventId, userID);
    }

    // ---------------------------------- methods for acquiring location-----------------------------
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Comment out to test:
                                // Toast.makeText(QRCodeScanActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                                Checkin.updateLocation(eventId, new GeoPoint(latitude, longitude));
                            } else {
                                Toast.makeText(QRCodeScanActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
