package com.example.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Utility class for uploading and downloading QR codes to and from Firebase Storage.
 */
public class QRCodeFirebase {

    /**
     * Uploads the given QR code bitmap to Firebase Storage at the specified path.
     *
     * @param bitmap   The QR code bitmap to upload.
     * @param pathInStorage The path in Firebase Storage to upload the QR code bitmap to.
     * @param callback The callback to be invoked upon upload success or failure.
     */
    public static void uploadQRCodeToFirebaseStorage(Bitmap bitmap, String pathInStorage, UploadCallback callback){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(pathInStorage);
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (callback != null) {
                callback.onSuccess(uri.toString());
            }
        })).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Saves the given check-in QR code bitmap to Firebase Storage.
     *
     * @param qrCodeBitmap The check-in QR code bitmap to save.
     * @param context      The context to access resources and callbacks.
     */
    public static void saveCheckInQRCode(Bitmap qrCodeBitmap, Context context) {
        if (qrCodeBitmap != null) {
            String pathInStorage = "event_check_ins/" + System.currentTimeMillis() + ".png";
            uploadQRCodeToFirebaseStorage(qrCodeBitmap, pathInStorage, (UploadCallback) context);
        }
    }

    /**
     * Saves the given event page QR code bitmap to Firebase Storage.
     *
     * @param qrCodeBitmap The event page QR code bitmap to save.
     * @param context      The context to access resources and callbacks.
     */
    public static void saveEventPageQRCode(Bitmap qrCodeBitmap, Context context) {
        if (qrCodeBitmap != null) {
            String pathInStorage = "event_pages/" + System.currentTimeMillis() + ".png";
            uploadQRCodeToFirebaseStorage(qrCodeBitmap, pathInStorage, (UploadCallback) context);
        }
    }

    /**
     * Saves the given user QR code bitmap to Firebase Storage.
     *
     * @param qrCodeBitmap The user QR code bitmap to save.
     * @param context      The context to access resources and callbacks.
     */
    public static void saveUserQRCode(Bitmap qrCodeBitmap, Context context) {
        if (qrCodeBitmap != null) {
            String pathInStorage = "user_info/" + System.currentTimeMillis() + ".png";
            uploadQRCodeToFirebaseStorage(qrCodeBitmap, pathInStorage, (UploadCallback) context);
        }
    }

    /**
     * Downloads an image from the given URL using Glide and invokes the callback upon success or failure.
     *
     * @param context  The context to access resources.
     * @param url      The URL of the image to download.
     * @param callback The callback to be invoked upon download success or failure.
     */
    public static void downloadImage(Context context, String url, DownloadCallback callback) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onDownloaded(resource);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (callback != null) {
                            callback.onError(new Exception("Failed to download image"));
                        }
                    }
                });
    }

}
