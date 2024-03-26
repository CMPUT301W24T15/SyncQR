package com.example.sync;


import java.io.Serializable;
import java.util.Random;

public class EventIDGenerator implements Serializable {

    private int eventID;
    private static final Random random = new Random();

    public EventIDGenerator() {
        this.eventID = random.nextInt(Integer.MAX_VALUE);
    }

    public int getEventID() {
        return eventID;
    }




}
