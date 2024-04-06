package com.example.sync.organizer;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.Event;
import com.example.sync.Notification;
import com.example.sync.QRCodeGenerator;
import com.example.sync.R;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.util.ArrayList;

public class QrCodeDialog extends DialogFragment {
    private static String input;
    ImageView qrcode;
    Button generate;
    Button regenerate;
    Button share;
    private Bitmap bitmap;
    private static boolean reuse = false;
    private static boolean initialized = false;

    static QrCodeDialog newInstance(String inputString) {
        // create the fragment instance
        QrCodeDialog dialog = new QrCodeDialog();

        Bundle args = new Bundle();
        args.putString("inputString", inputString);
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
        regenerate = view.findViewById(R.id.reuse_button);
        share = view.findViewById(R.id.share_button);

        // get the input string (optional)
        Bundle args = getArguments();
        input = args.getString("inputString");

        // first time generate QR code
        if (!initialized) {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(requireContext(), "Please generate a new QR code first", Toast.LENGTH_LONG).show();
                }
            });
            regenerate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(requireContext(), "Please generate a new QR code first", Toast.LENGTH_LONG).show();
                }
            });

            generate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initialized = true;
                    generateQRcode();
                    reuseMode();
                }
            });

        } else {
            bitmap = QRCodeGenerator.generateQRCodeBitmap(input, 300, 300);
            qrcode.setImageBitmap(bitmap);
            reuseMode();
        }

        return builder.create();
    }

    private void generateQRcode(){
        if (!reuse) {
            bitmap = QRCodeGenerator.generateQRCodeBitmap("checkin"+input, 300, 300);
        } else {
            String random = RandomStringGenerator.generateRandomString(20);
            bitmap = QRCodeGenerator.generateQRCodeBitmap("checkin"+random, 300, 300);
        }
        qrcode.setImageBitmap(bitmap);

    }

    private void reuseMode(){
        regenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reuse = false;
                generateQRcode();
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reuse = true;
                generateQRcode();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapToUri());
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });
    }

    private Uri bitmapToUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ContentResolver resolver = requireContext().getContentResolver();
        String path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null);
        Uri uri = Uri.parse(path);
        return uri;
    }
}