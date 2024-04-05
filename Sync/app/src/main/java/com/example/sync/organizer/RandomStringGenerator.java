package com.example.sync.organizer;
import java.util.Random;

public class RandomStringGenerator {

    public static String generateRandomString(int length) {
        // alphabet
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        // random generator
        Random random = new Random();

        // shuffle
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            char randomChar = charset.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
