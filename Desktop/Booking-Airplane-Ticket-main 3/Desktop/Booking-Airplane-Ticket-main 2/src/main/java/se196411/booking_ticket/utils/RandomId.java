package se196411.booking_ticket.utils;

import java.security.SecureRandom;

public class RandomId {
    private static final SecureRandom RANDOM = new SecureRandom();
    public static String    generateRandomId(int characterLength, int numberLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < characterLength; i++) {
            char letter = (char) ('A' + RANDOM.nextInt(26));
            sb.append(letter);
        }
        for (int i = 0; i < numberLength; i++) {
            int digit = RANDOM.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }
}
