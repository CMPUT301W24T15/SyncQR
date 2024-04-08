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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.sync.QRCodeGenerator;
import com.example.sync.R;

import java.io.ByteArrayOutputStream;

public class PromoDialog extends DialogFragment {
    ImageView qrcode;
    Button share;

    static PromoDialog newInstance(String eventId) {
        // create the fragment instance
        PromoDialog dialog = new PromoDialog();

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
        builder.setTitle("Tab to Share");

        // link to a view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.promo_dialog, null);
        builder.setView(view);

        qrcode = view.findViewById(R.id.qrCodeImageView);
        share = view.findViewById(R.id.share_button);

        // get the input string (optional)
        Bundle args = getArguments();
        String id = args.getString("eventId");

        // generate promotion QRcode
        String qrcodeText = "SyncQRevent"+id;
        Bitmap bitmap = QRCodeGenerator.generateQRCodeBitmap(qrcodeText, 300, 300);
        qrcode.setImageBitmap(bitmap);

        // share Button
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapToUri(bitmap));
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });

        return builder.create();
    }

    private Uri bitmapToUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        ContentResolver resolver = requireContext().getContentResolver();
        String path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null);
        return Uri.parse(path);
    }
}
