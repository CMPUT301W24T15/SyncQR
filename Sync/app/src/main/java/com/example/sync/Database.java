package com.example.sync;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
/**
 * This is a class that keeps track of database
 */
public class Database {
    /**
     * This method add entry to the firebase database
     */
    public static void addEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }
    /**
     * This method change entry to the firebase database
     */
    public static void changeEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }
}
