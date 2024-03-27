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

    @Override
    public void signUpUser(String eventId, String userId) {
        eventSignUpUsers.computeIfAbsent(eventId, k -> new ArrayList<>()).add(userId);
        // Consider saving to Firestore here
    }

    @Override
    public void userCheckin(String eventId, String userId, String location, boolean isFirstCheckin) {
        // Update check-in counts
        eventCheckinCounts.computeIfAbsent(eventId, k -> new HashMap<>()).merge(userId, 1, Integer::sum);

        // Add to current check-ins if it's the first time
        if (isFirstCheckin) {
            eventCurrentCheckins.computeIfAbsent(eventId, k -> new ArrayList<>()).add(userId);
        }

        // Update location if provided
        if (location != null && !location.isEmpty()) {
            eventLocations.computeIfAbsent(eventId, k -> new HashMap<>()).put(userId, location);
        }

        // Save updated data to Firestore here
    }

    @Override
    public void userCheckout(String eventId, String userId) {
        // Remove from current check-ins
        List<String> currentCheckins = eventCurrentCheckins.getOrDefault(eventId, new ArrayList<>());
        currentCheckins.remove(userId);
        // Consider updating Firestore here for checkout
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