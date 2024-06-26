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

/**
 * A dialog fragment for generating, reusing, saving, and sharing QR codes.
 */
public class QrCodeDialog extends DialogFragment {
    /**
     * The view to display a qrcode image
     */
    ImageView qrcode;
    /**
     * The view tapped to generate a new qrcode
     */
    Button generate;
    /**
     * The view tapped to upload and recognize an existed qrcode
     */
    Button reuse;
    /**
     * The share button
     */
    Button share;
    /**
     * The save button
     */
    Button save;
    /**
     * The image in bitmap format
     */
    Bitmap bitmap;
    /**
     * The string that qrcode represented
     */
    String qrcodeText;
    /**
     * The image picker
     */
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    /**
     * Creates a new instance of QrCodeDialog with the given event ID.
     * @param eventId The ID of the event.
     * @return QrCodeDialog
     */
    static QrCodeDialog newInstance(String eventId) {
        // create the fragment instance
        QrCodeDialog dialog = new QrCodeDialog();

        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Called to create the dialog shown in the fragment.
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return AlertDialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tab to Generate QR Code");

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(com.example.sync.R.layout.qr_code_organizer, null);
        builder.setView(view);

        qrcode = view.findViewById(com.example.sync.R.id.qrCodeImageView);
        generate = view.findViewById(com.example.sync.R.id.generate_button);
        reuse = view.findViewById(com.example.sync.R.id.reuse_button);
        share = view.findViewById(com.example.sync.R.id.share_button);
        save = view.findViewById(com.example.sync.R.id.save_button);

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

    /**
     * Generates a QR code for the given event ID and displays it.
     * @param id The ID of the event.
     */
    private void generateQRcode(String id){
        qrcodeText = "SyncQRcheckin"+id;
        bitmap = QRCodeGenerator.generateQRCodeBitmap(qrcodeText, 300, 300);
        qrcode.setImageBitmap(bitmap);
    }

    /**
     * Recognizes the QR code from the provided URI and extracts the text.
     * @param uri The URI of the image containing the QR code.
     * @return The extracted text from the QR code.
     */
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

    /**
     * Converts a bitmap image to a Uri.
     * @return The Uri of the converted image.
     */
    private Uri bitmapToUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ContentResolver resolver = requireContext().getContentResolver();
        String path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null);
        Uri uri = Uri.parse(path);
        return uri;
    }

    /**
     * Enables the save and share functionality for the generated QR code.
     * @param id The ID of the event.
     */
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