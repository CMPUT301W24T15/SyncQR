package com.example.sync;

import java.security.SecureRandom;

public class UserIDGenerator {

    private static final String CHARACTER_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 20; // Default length of the ID
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random string ID of default length.
     *
     * @return A random string ID.
     */
    public static String generateUserID() {
        return generateRandomID(DEFAULT_LENGTH);
    }

    /**
     * Generates a random string ID of specified length.
     *
     * @param length The length of the ID to generate.
     * @return A random string ID.
     */
    public static String generateRandomID(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be at least 1");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTER_SET.length());
            sb.append(CHARACTER_SET.charAt(randomIndex));
        }
        return sb.toString();
    }
}
