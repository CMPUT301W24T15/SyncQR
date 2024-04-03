package com.example.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test cases for the UserIDGenerator class.
 */
public class IDGeneratorTest {

    /**
     * Tests the generateUserID() method.
     * Ensures that a non-null string of length 20 is returned.
     */
    @Test
    public void testGenerateUserID() {
        String userID = UserIDGenerator.generateUserID();
        assertNotNull(userID);
        assertEquals(20, userID.length());
    }

    /**
     * Tests the generateRandomID(int length) method with a specified length.
     * Ensures that a non-null string of the specified length is returned.
     */
    @Test
    public void testGenerateRandomIDWithLength() {
        int length = 10;
        String randomID = UserIDGenerator.generateRandomID(length);
        assertNotNull(randomID);
        assertEquals(length, randomID.length());
    }

    /**
     * Tests the generateRandomID(int length) method with a negative length.
     * Ensures that an IllegalArgumentException is thrown.
     */
    @Test
    public void testGenerateRandomIDWithNegativeLength() {
        try {
            UserIDGenerator.generateRandomID(-5);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Length must be at least 1", e.getMessage());
        }
    }

    /**
     * Tests the generateRandomID(int length) method with a zero length.
     * Ensures that an IllegalArgumentException is thrown.
     */
    @Test
    public void testGenerateRandomIDWithZeroLength() {
        try {
            UserIDGenerator.generateRandomID(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Length must be at least 1", e.getMessage());
        }
    }
}
