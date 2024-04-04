package com.example.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodeFileSaver {

    /**
     * Saves the given bitmap as an image file in the device's public gallery.
     *
     * @param context   The context to access the application's resources.
     * @param bitmap    The bitmap to save as an image file.
     * @param albumName The name of the album to save the image file in.
     * @return The URI of the saved image file, or null if saving fails.
     */
    public static Uri saveBitmapToGallery(Context context, Bitmap bitmap, String albumName) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "QRCode_" + timeStamp + ".jpg";

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + albumName);

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri != null) {
            try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
                boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                if (!saved) {
                    Log.e("QRCodeFileSaver", "Failed to save bitmap");
                    return null;
                }
            } catch (IOException e) {
                Log.e("QRCodeFileSaver", "Error saving image", e);
                return null;
            }
        }

        return imageUri;
    }
}