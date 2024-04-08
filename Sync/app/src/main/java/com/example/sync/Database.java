package com.example.sync;

import android.graphics.Bitmap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Helper class for performing database operations.
 */
public class Database {

    public interface Callback{
        void onSuccess(String uri);
    }

    /**
     * Stores a bitmap image in Firebase Storage at the specified path.
     *
     * @param bitmap         The bitmap image to store.
     * @param pathInStorage  The path within Firebase Storage to store the image.
     * @param callback       The callback interface to handle the result of the asynchronous operation.
     *                       The download URL of the stored image will be returned through the
     *                       {@link Callback#onSuccess(String)} method.
     */
    public static void storeImage(Bitmap bitmap, String pathInStorage, Callback callback){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(pathInStorage);
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (callback != null) {
                callback.onSuccess(uri.toString());
            }
        }));
    }
}

