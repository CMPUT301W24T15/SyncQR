package com.example.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckinImpl implements Checkin {
    private final HashMap<String, HashMap<String, String>> checkinUsers = new HashMap<>();
    private final List<String> currentCheckins = new ArrayList<>();
    private final HashMap<String, String> userLocations = new HashMap<>();
    private int nextId = 1; // To generate sequential IDs

    @Override
    public void userCheckin(String userId, HashMap<String, String> userDetails, boolean grantLocationPermission) {
        String checkinId = "user_" + nextId++;
        checkinUsers.put(checkinId, userDetails);
        currentCheckins.add(checkinId);

        if (grantLocationPermission) {
            String location = userDetails.get("location"); // Assuming location is part of userDetails
            if (location != null) {
                userLocations.put(checkinId, location);
            }
        }

        // Save to Firestore
        Database.addEntry("checkinUsers", checkinId, userDetails);
        if (grantLocationPermission) {
            Database.addEntry("userLocations", checkinId, userDetails); // Adjust this according to how you want to store location info
        }
    }

    @Override
    public void userCheckout(String userId) {
        currentCheckins.remove(userId);
        userLocations.remove(userId);
        // Update Firestore if necessary. This might require fetching the existing document and updating it.
    }

    @Override
    public HashMap<String, HashMap<String, String>> getCheckins() {
        return checkinUsers;
    }

    @Override
    public List<String> getCurrentCheckins() {
        return currentCheckins;
    }

    @Override
    public HashMap<String, String> getLocations() {
        return userLocations;
    }
}