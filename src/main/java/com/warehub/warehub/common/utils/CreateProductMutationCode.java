package com.warehub.warehub.common.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CreateProductMutationCode {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private final AtomicInteger sequenceCounter = new AtomicInteger(1);

    /**
     * Generates a simplified product mutation ID that is frontend-friendly
     * while still maintaining uniqueness.
     * Format: PM-[DATE]-[RANDOM]-[SEQ]
     * Example: PM-250312-7426-1
     *
     * @return A simplified unique product mutation ID
     */
    public String generateProductMutationId() {
        // Get current date in format yyMMdd (e.g., 250312 for March 12, 2025)
        String dateCode = LocalDateTime.now().format(DATE_FORMATTER);

        // Generate a 4-digit random number
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 10000);

        // Get next sequence number (resets at 10000)
        int sequence = sequenceCounter.getAndUpdate(n -> n < 9999 ? n + 1 : 1);

        // Combine parts with hyphens for readability
        return String.format("PM-%s-%d-%d", dateCode, randomPart, sequence);
    }

    /**
     * Generates a simplified product mutation ID with a custom category code.
     * Format: [CATEGORY]-[DATE]-[SEQ]
     * Example: ELEC-250312-42
     *
     * @param categoryCode A short category code (max 4 chars)
     * @return A simplified unique product mutation ID with category
     */
    public String generateProductMutationId(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            return generateProductMutationId();
        }

        // Format category code: uppercase, max 4 chars
        String code = categoryCode.trim().toUpperCase();
        if (code.length() > 4) {
            code = code.substring(0, 4);
        }

        // Get current date in format yyMMdd
        String dateCode = LocalDateTime.now().format(DATE_FORMATTER);

        // Get next sequence number (resets at 1000)
        int sequence = sequenceCounter.getAndUpdate(n -> n < 999 ? n + 1 : 1);

        // Combine parts
        return String.format("%s-%s-%d", code, dateCode, sequence);
    }
}
