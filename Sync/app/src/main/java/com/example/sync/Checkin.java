package com.example.sync;

import java.util.HashMap;
import java.util.List;

public interface Checkin {
    void userCheckin(String userId, HashMap<String, String> userDetails, boolean grantLocationPermission);
    void userCheckout(String userId);
    HashMap<String, HashMap<String, String>> getCheckins();
    List<String> getCurrentCheckins();
    HashMap<String, String> getLocations();
}