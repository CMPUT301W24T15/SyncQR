package com.example.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class QRCodeActivity extends AppCompatActivity {

    private static ImageView qrCodeImageView;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        progressBar = findViewById(R.id.progressBar);

        // Get the userID from the intent
        String userID = getIntent().getStringExtra("userID");
        if (userID != null) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            generateAndSaveUserQRCode(userID, 300, 300, this);
        }
    }

    public static void generateAndSaveUserQRCode(String userID, int width, int height, Context context) {
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCodeBitmap(userID, width, height);
        if (qrCodeBitmap != null) {
            String pathInStorage = "user_info/" + System.currentTimeMillis() + ".png";
            QRCodeFirebase.uploadQRCodeToFirebaseStorage(qrCodeBitmap, pathInStorage, new UploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    // Assuming you have a way to download the image from the URL and convert it to a Bitmap
                    QRCodeFirebase.downloadImage(context, downloadUrl, new DownloadCallback() {
                        @Override
                        public void onDownloaded(Bitmap bitmap) {
                            qrCodeImageView.setImageBitmap(bitmap);
                            progressBar.setVisibility(ProgressBar.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle download error
                            progressBar.setVisibility(ProgressBar.GONE);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle upload error
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            });
        } else {
            // Handle QR Code generation failure
            progressBar.setVisibility(ProgressBar.GONE);
        }
    }

}
