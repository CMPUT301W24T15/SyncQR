package com.example.sync;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        if ("Administrator".equals(scannedData)) {
            // Handle Administrator action
            Intent intent = new Intent(QRCodeScanActivity.this, AdministratorDashboard.class);
            startActivity(intent);
        } else if (scannedData.startsWith("SyncQRevent")) {
            // Extract the event ID from the scanned data
            String eventId = extractSubstringAfterDelimiter(scannedData, "t");
            // Start EventDetailsActivity with the extracted event ID
            EventDetailsActivity.startWithEventId(this, eventId);
        } else {
            // Handle any other unexpected QR code data
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Checkin System")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> qrCodes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String qrCode = document.getString("qrcode");
                                if (qrCode != null) {
                                    qrCodes.add(qrCode);
                                }
                            }
                            // Now you have all the QR codes in qrCodes list
                            if(compareScannedData(scannedData, qrCodes)){
                                String userID = getIntent().getStringExtra("userID");
                                fetchLocationAndCheckIn(userID);
                            } else {
                                Toast.makeText(this, "Unrecognized QR Code", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("DatabaseError", "Error getting documents: ", task.getException());
                        }
                    });
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

    /**
     * Attempts to fetch the device's last known location and use it to check in the user
     * at an event. This method first checks if the location permission has been granted.
     * If permission is granted, it attempts to acquire the last known location. Otherwise,
     * it requests the necessary permission.
     *
     * @param userID The user ID for whom the check-in is being performed. This ID is used
     *               in conjunction with the event ID to record the check-in.
     */
    private void fetchLocationAndCheckIn(String userID) {
        checkLocationPermission();
        Checkin.checkInForUser(eventId, userID);
    }

    /**
     * Requests the last known location of the device from the fused location provider.
     * If successful and the location is not null, it updates the event's location with
     * the current latitude and longitude. If the location is null, a toast message is
     * displayed to the user. This method requires the ACCESS_FINE_LOCATION permission to
     * have been granted.
     */
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
                                Checkin.updateLocation(eventId, new GeoPoint(latitude, longitude));
                            } else {
                                Toast.makeText(QRCodeScanActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Checks if the ACCESS_FINE_LOCATION permission has been granted. If it has not,
     * the permission is requested. If it has been granted, the getLastLocation method
     * is called to attempt to fetch the device's last known location.
     */
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

    /**
     * Callback for the result from requesting permissions. This method is invoked when the user
     * responds to the permission request. If the permission is granted, getLastLocation is
     * called to fetch the device's last location. If the permission is denied, a toast
     * message is shown to the user.
     *
     * @param requestCode  The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
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


    /**
     * Compares the provided scanned data against a list of QR codes to check for a match.
     * This method iterates through the list of QR codes and checks if any of them equals
     * the scanned data. If a match is found, it logs a message indicating success and
     * can perform further actions as required by the application logic. If no match is
     * found, it logs a different message indicating failure.
     *
     * @param scannedData The data obtained from scanning a QR code, expected to be a String.
     * @param qrCodes     A list of QR codes (as Strings) retrieved from the database against which
     *                    the scanned data is to be compared.
     * @return
     */
    private boolean compareScannedData(String scannedData, List<String> qrCodes) {
        boolean isMatchFound = false;

        for (String qrCode : qrCodes) {
            if (scannedData.equals(qrCode)) {
                isMatchFound = true;
                break;
            }
        }
        return isMatchFound;
    }

}
