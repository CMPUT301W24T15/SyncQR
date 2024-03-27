package com.example.sync;

import java.util.HashMap;
import java.util.List;

public interface Checkin {
    void signUpUser(String eventId, String userId);
    void userCheckin(String eventId, String userId, String location, boolean isFirstCheckin);
    void userCheckout(String eventId, String userId);
    List<String> getSignUpUsers(String eventId);
    HashMap<String, Integer> getCheckinCounts(String eventId);
    List<String> getCurrentCheckins(String eventId);
    HashMap<String, String> getLocations(String eventId);
}
