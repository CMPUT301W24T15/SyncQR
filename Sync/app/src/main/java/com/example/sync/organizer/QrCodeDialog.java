package com.example.sync.organizer;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.Checkin;
import com.example.sync.QRCodeGenerator;
import com.example.sync.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;

public class QrCodeDialog extends DialogFragment {
    ImageView qrcode;
    Button generate;
    Button reuse;
    Button share;
    Button save;
    Bitmap bitmap;
    String qrcodeText;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    static QrCodeDialog newInstance(String eventId) {
        // create the fragment instance
        QrCodeDialog dialog = new QrCodeDialog();

        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        dialog.setArguments(args);

        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tab to Generate QR Code");

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.qr_code_organizer, null);
        builder.setView(view);

        qrcode = view.findViewById(R.id.qrCodeImageView);
        generate = view.findViewById(R.id.generate_button);
        reuse = view.findViewById(R.id.reuse_button);
        share = view.findViewById(R.id.share_button);
        save = view.findViewById(R.id.save_button);

        // get the input string (optional)
        Bundle args = getArguments();
        String id = args.getString("eventId");

        // initialize a photo picker
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);


                        // display
                        qrcode.setImageURI(null);
                        qrcode.setImageURI(uri);

                        // Extract string
                        qrcodeText = recognize(uri);

                        if (!qrcodeText.equals("error")) {
                            enableSaveAndShare(id);
                        }


                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // share Button: can share only when QR code is ready
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Please generate a new QR code first", Toast.LENGTH_LONG).show();
            }
        });


        // new button: generate a unique QR code for this event
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRcode(id);
                enableSaveAndShare(id);
            }
        });

        // reuse button: invoke media picker
        reuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Upload your existed QRcode", Toast.LENGTH_LONG).show();

                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        return builder.create();
    }

    private void generateQRcode(String id){
        qrcodeText = "SyncQR"+id;
        bitmap = QRCodeGenerator.generateQRCodeBitmap(qrcodeText, 300, 300);
        qrcode.setImageBitmap(bitmap);
    }

    private String recognize(Uri uri){
        try {
            // Convert URI to Bitmap
            InputStream imageStream = getContext().getContentResolver().openInputStream(uri);
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
            Toast.makeText(getContext(), "Successfully extract string", Toast.LENGTH_LONG).show();

            return qrCodeText;

        } catch (Exception e) {
            Toast.makeText(getContext(), "Cannot Recognize a QRcode, try other picture", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return "error";
    }

    private Uri bitmapToUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ContentResolver resolver = requireContext().getContentResolver();
        String path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null);
        Uri uri = Uri.parse(path);
        return uri;
    }

    private void enableSaveAndShare(String id) {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapToUri());
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkin.saveQRcode(qrcodeText, id, new Checkin.Callback() {
                    @Override
                    public void onSavedQRCode(String text) {
                        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}