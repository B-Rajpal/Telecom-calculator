package com.telecom.aggregator.parser;

import com.telecom.aggregator.model.UsageRecord;

/**
 * Parses individual lines from input files into UsageRecord objects.
 * Ensures that malformed or invalid lines are gracefully handled.
 */
public class UsageRecordParser {

    /**
     * Parses a single pipe-separated line into a UsageRecord object.
     * Returns null if the line is invalid or incomplete.
     *
     * Expected format:
     * MobileNumber|Tower|4G|5G|Roaming
     *
     * @param line the input line from file
     * @return a valid UsageRecord or null if line is malformed
     */
    public UsageRecord parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            System.err.println("Skipping empty line.");
            return null;
        }

        String[] parts = line.trim().split("\\|");

        if (parts.length != 5) {
            System.err.println("Malformed line (wrong number of fields): " + line);
            return null;
        }

        try {
            String mobileNumber = parts[0].trim();
            String tower = parts[1].trim();
            int data4G = Integer.parseInt(parts[2].trim());
            int data5G = Integer.parseInt(parts[3].trim());
            String roamingStr = parts[4].trim();

            // Validate roaming flag explicitly: must be "Yes" or "No"
            boolean roaming;
            if (roamingStr.equalsIgnoreCase("Yes")) {
                roaming = true;
            } else if (roamingStr.equalsIgnoreCase("No")) {
                roaming = false;
            } else {
                System.err.println("Invalid roaming value (must be 'Yes' or 'No'): " + roamingStr);
                return null;
            }

            // Validate mobile number format (exactly 10 digits)
            if (!mobileNumber.matches("\\d{10}")) {
                System.err.println("Invalid mobile number format: " + mobileNumber);
                return null;
            }

            // Validate non-negative data usage
            if (data4G < 0 || data5G < 0) {
                System.err.println("Negative data usage in line: " + line);
                return null;
            }

            return new UsageRecord(mobileNumber, tower, data4G, data5G, roaming);

        } catch (NumberFormatException e) {
            System.err.println("Failed to parse numeric values in line: " + line);
        } catch (Exception e) {
            System.err.println("Unexpected error while parsing line: " + line);
            e.printStackTrace(); // developer debug aid
        }

        return null;
    }
}
