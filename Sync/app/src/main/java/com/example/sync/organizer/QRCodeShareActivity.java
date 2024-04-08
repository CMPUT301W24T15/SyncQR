package com.example.sync.organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.sync.QRCodeGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity for sharing a generated QR code image.
 */
public class QRCodeShareActivity extends AppCompatActivity {
    /**
     * The view for qrcode to be displayed
     */
    private static ImageView qrCodeImageView;

    /**
     * The generated QR code in bitmap
     */
    private Bitmap qrCodeBitmap;

    /**
     * Create a new UI interface
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sync.R.layout.qr_code);

        // Initialize views
        qrCodeImageView = findViewById(com.example.sync.R.id.qrCodeImageView);

        // Set up share button click listener
        FloatingActionButton shareButton = findViewById(com.example.sync.R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

        // Get the inputID from the intent and generate QR code
        String inputID = getIntent().getStringExtra("inputID");
        if (inputID != null) {
            generateQRCode(inputID, 300, 300, this);
        }
    }

    /**
     * Inflate the menu resource for this activity's toolbar
     * @param menu The options menu in which you place your items.
     *
     * @return Boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.sync.R.menu.qr_code_menu, menu);
        return true;
    }

    /**
     * Handle menu item clicks
     * @param item The menu item that was selected.
     *
     * @return Boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == com.example.sync.R.id.action_share) {
            shareQRCode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Share the generated QR code
     * @see Intent
     */
    private void shareQRCode() {
        if (qrCodeBitmap != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(qrCodeBitmap));
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
        } else {
            Toast.makeText(this, "QR Code not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generate QR code bitmap
     * @param inputID The input string that needs to be converted
     * @param width The width of the qrcode
     * @param height The height of the qrcode
     * @param context The context of current dialog
     */
    private void generateQRCode(String inputID, int width, int height, QRCodeShareActivity context) {
        qrCodeBitmap = QRCodeGenerator.generateQRCodeBitmap(inputID, width, height);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        } else {
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the URI of a bitmap.
     * @param bmp The bitmap to convert to URI.
     * @return Uri.
     */

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalCacheDir(), "images/qr_code_image.png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
