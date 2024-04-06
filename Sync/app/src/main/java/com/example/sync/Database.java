package com.example.sync;

import android.graphics.Bitmap;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Helper class for performing database operations.
 */
public class Database {

    public interface Callback{
        void onSuccess(String uri);
    }

    /**
     * Adds an entry to the specified collection in Firestore.
     *
     * @param collectionName The name of the collection to add the entry to.
     * @param key            The key of the document.
     * @param value          The value to be added.
     */
    public static void addEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }

    /**
     * Changes an existing entry in the specified collection in Firestore.
     *
     * @param collectionName The name of the collection containing the entry to be changed.
     * @param key            The key of the document to be changed.
     * @param value          The new value to set for the entry.
     */
    public static void changeEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }

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

