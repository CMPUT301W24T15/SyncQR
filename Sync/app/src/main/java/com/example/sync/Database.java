package com.example.sync;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Database {

    public static void addEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }

    public static void changeEntry(String collectionName, String key, HashMap<String, String> value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference currentRef = db.collection(collectionName);
        currentRef.document(key).set(value);
    }
}
