package com.example.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckinImpl implements Checkin {
    private final HashMap<String, List<String>> eventSignUpUsers = new HashMap<>();
    private final HashMap<String, HashMap<String, Integer>> eventCheckinCounts = new HashMap<>();
    private final HashMap<String, List<String>> eventCurrentCheckins = new HashMap<>();
    private final HashMap<String, HashMap<String, String>> eventLocations = new HashMap<>();
    private final String EVENTS_COLLECTION = "events";
    @Override
    public void signUpUser(String eventId, String userId) {
        List<String> signUpUsers = eventSignUpUsers.computeIfAbsent(eventId, k -> new ArrayList<>());
        signUpUsers.add(userId);

        // Consider this a subcollection under each event for scalability
        Database.addEntry(EVENTS_COLLECTION + "/" + eventId + "/signUpUsers", userId, new HashMap<String, String>() {{
            put("userId", userId);
        }});
    }

    @Override
    public void userCheckin(String eventId, String userId, String location, boolean isFirstCheckin) {
        HashMap<String, Integer> checkinCountsInt = eventCheckinCounts.computeIfAbsent(eventId, k -> new HashMap<>());
        checkinCountsInt.merge(userId, 1, Integer::sum);

        // Convert check-in counts from Integer to String for Firestore
        HashMap<String, String> checkinCountsStr = new HashMap<>();
        for (Map.Entry<String, Integer> entry : checkinCountsInt.entrySet()) {
            checkinCountsStr.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

        if (isFirstCheckin) {
            List<String> currentCheckins = eventCurrentCheckins.computeIfAbsent(eventId, k -> new ArrayList<>());
            currentCheckins.add(userId);

            // Update Firestore for current check-ins
            Database.addEntry(EVENTS_COLLECTION + "/" + eventId + "/currentCheckins", userId, new HashMap<String, String>() {{
                put("checkinId", userId); // Assuming you want to track by userId for check-ins
            }});
        }

        if (location != null && !location.isEmpty()) {
            HashMap<String, String> locations = eventLocations.computeIfAbsent(eventId, k -> new HashMap<>());
            locations.put(userId, location);

            // Update Firestore for user locations
            Database.addEntry(EVENTS_COLLECTION + "/" + eventId + "/userLocations", userId, new HashMap<String, String>() {{
                put("location", location);
            }});
        }

        // Update Firestore with the String-based check-in counts
        Database.changeEntry(EVENTS_COLLECTION + "/" + eventId, "checkinCounts", checkinCountsStr);
    }

    @Override
    public void userCheckout(String eventId, String userId) {
        List<String> currentCheckins = eventCurrentCheckins.getOrDefault(eventId, new ArrayList<>());
        currentCheckins.remove(userId);

        // Assuming direct Firestore update for current check-ins on checkout
        Database.changeEntry(EVENTS_COLLECTION + "/" + eventId + "/currentCheckins", userId, new HashMap<String, String>() {{
            put("checkedOut", "true"); // Example field update for checkout
        }});

        // Consider how you want to reflect checkout in Firestore (e.g., removing from current check-ins, updating a status, etc.)
    }

    @Override
    public List<String> getSignUpUsers(String eventId) {
        return eventSignUpUsers.getOrDefault(eventId, new ArrayList<>());
    }

    @Override
    public HashMap<String, Integer> getCheckinCounts(String eventId) {
        return eventCheckinCounts.getOrDefault(eventId, new HashMap<>());
    }

    @Override
    public List<String> getCurrentCheckins(String eventId) {
        return eventCurrentCheckins.getOrDefault(eventId, new ArrayList<>());
    }

    @Override
    public HashMap<String, String> getLocations(String eventId) {
        return eventLocations.getOrDefault(eventId, new HashMap<>());
    }
}