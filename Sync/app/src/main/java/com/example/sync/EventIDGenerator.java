package com.example.sync;

import java.util.Random;

/**
 * Class to randomly generate event id.
 */
public class EventIDGenerator {

    private static final Random random = new Random();

    /**
     *
     * @return an integer as eventid that's randomly generated
     */
    public static int generate() {
        return random.nextInt(Integer.MAX_VALUE);
    }

}
