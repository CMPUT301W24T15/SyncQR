package com.example.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodeFileSaver {

    /**
     * Saves the given bitmap as an image file in the specified directory.
     *
     * @param context The context to access the application's resources.
     * @param bitmap  The bitmap to save as an image file.
     * @param folderName The name of the folder to save the image file in.
     * @return The URI of the saved image file, or null if saving fails.
     */
    public static Uri saveBitmapAsImage(Context context, Bitmap bitmap, String folderName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "QRCode_" + timeStamp + ".png";

        // Get the directory to save the image file
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), folderName);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("QRCodeFileSaver", "Failed to create directory");
                return null;
            }
        }

        File imageFile = new File(storageDir, imageFileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

