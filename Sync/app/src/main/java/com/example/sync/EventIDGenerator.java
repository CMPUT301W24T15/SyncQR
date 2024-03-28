package com.example.sync;

import java.util.Random;

public class EventIDGenerator {

    private static final Random random = new Random();

    public static int generate() {
        return random.nextInt(Integer.MAX_VALUE);
    }





}
