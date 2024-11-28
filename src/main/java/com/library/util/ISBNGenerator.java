package com.library.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ISBNGenerator {
    public static void main(String[] args) {
        List<String> isbns = generateISBNs(100);
        for (String isbn : isbns) {
            System.out.println(isbn);
        }
    }

    public static List<String> generateISBNs(int count) {
        List<String> isbns = new ArrayList<>();
        Random random = new Random();

        while (isbns.size() < count) {
            // Generate first 12 digits randomly
            StringBuilder base = new StringBuilder("978");
            for (int i = 0; i < 9; i++) {
                base.append(random.nextInt(10));
            }

            // Calculate checksum
            int checksum = calculateChecksum(base.toString());
            isbns.add(base.append(checksum).toString());
        }
        return isbns;
    }

    private static int calculateChecksum(String base) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            int digit = Character.getNumericValue(base.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
}
