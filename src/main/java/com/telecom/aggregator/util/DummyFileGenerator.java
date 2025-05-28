package com.telecom.aggregator.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class to generate sample telecom usage files.
 * Creates random but reproducible dummy data for testing purposes.
 */
public class DummyFileGenerator {

    private static final Random random = new Random();

    // Mobile numbers pool (some will repeat between files)
    private static final List<String> mobileNumbers = Arrays.asList(
            "9000600600", "9000600601", "9000600602", "9000600603", "9000600604",
            "9000600605", "9000600606", "9000600607", "9000600608", "9000600609"
    );

    // Sample towers (randomly assigned)
    private static final List<String> towers = Arrays.asList(
            "InAir1234", "InAir5678", "SkyNet777", "FiberHub1", "RoamLink9"
    );

    /**
     * Generates two dummy input files with 30 records each.
     *
     * @param directory path to the directory where files will be written
     */
    public static void generate(Path directory) {
        try {
            // Create directory if it doesn’t exist
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Generate 2 files
            generateSingleFile(directory.resolve("usage_file_1.txt"), 30);
            generateSingleFile(directory.resolve("usage_file_2.txt"), 30);

            System.out.println("Dummy input files generated in: " + directory.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error generating dummy input files.");
            e.printStackTrace();
        }
    }

    /**
     * Creates a single file with a specified number of dummy lines.
     *
     * @param filePath target file path
     * @param count    number of lines to write
     */
    private static void generateSingleFile(Path filePath, int count) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (int i = 0; i < count; i++) {
                String line = generateRandomUsageLine();
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Failed to write to dummy file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Creates one random usage line in the format:
     * MobileNumber|Tower|4G|5G|Roaming
     */
    private static String generateRandomUsageLine() {
        String mobile = mobileNumbers.get(random.nextInt(mobileNumbers.size()));
        String tower = towers.get(random.nextInt(towers.size()));
        int data4G = random.nextInt(5000); // up to ~5MB
        int data5G = random.nextInt(8000); // up to ~8MB
        String roaming = random.nextBoolean() ? "Yes" : "No";

        return String.format("%s|%s|%d|%d|%s", mobile, tower, data4G, data5G, roaming);
    }
}
