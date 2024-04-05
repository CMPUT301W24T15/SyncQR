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
import com.example.sync.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity for sharing a generated QR code image.
 */
public class QRCodeShareActivity extends AppCompatActivity {

    private static ImageView qrCodeImageView;
    private static ProgressBar progressBar;
    private Bitmap qrCodeBitmap; // Store the generated QR code bitmap here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);

        // Initialize views
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        progressBar = findViewById(R.id.progressBar);

        // Set up share button click listener
        FloatingActionButton shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

        // Get the inputID from the intent and generate QR code
        String inputID = getIntent().getStringExtra("inputID");
        if (inputID != null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            generateQRCode(inputID, 300, 300, this);
        }
    }

    // Inflate the menu resource for this activity's toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_code_menu, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareQRCode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Share the generated QR code
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

    // Generate QR code bitmap
    private void generateQRCode(String inputID, int width, int height, QRCodeShareActivity context) {
        qrCodeBitmap = QRCodeGenerator.generateQRCodeBitmap(inputID, width, height);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            progressBar.setVisibility(ProgressBar.GONE);
        } else {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    // Get the URI of a bitmap
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
