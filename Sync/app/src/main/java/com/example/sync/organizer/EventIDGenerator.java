package com.example.sync.organizer;

import java.io.Serializable;
import java.util.Random;

public class EventIDGenerator implements Serializable {

    private int eventID;
    private static final Random random = new Random();

    public EventIDGenerator() {
        // Assuming you want a positive integer in the range of typical int values
        // You can limit the range of the generated number if needed
        this.eventID = random.nextInt(Integer.MAX_VALUE);
    }

    public int getEventID() {
        return eventID;
    }

}
