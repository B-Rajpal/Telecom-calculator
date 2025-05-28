package com.telecom.aggregator.core;

import com.telecom.aggregator.model.UsageRecord;
import com.telecom.aggregator.parser.UsageRecordParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/**
 * Handles reading and parsing input files.
 * Uses a streaming approach for performance and memory efficiency.
 */
public class FileProcessor {

    private final UsageRecordParser parser;
    private final DataAggregator aggregator;

    /**
     * Constructs a FileProcessor with dependencies.
     *
     * @param parser     record parser to convert lines into UsageRecord objects
     * @param aggregator data aggregator to collect parsed usage
     */
    public FileProcessor(UsageRecordParser parser, DataAggregator aggregator) {
        this.parser = parser;
        this.aggregator = aggregator;
    }

    /**
     * Processes all files provided.
     *
     * @param files list of file paths to process
     */
    public void processFiles(List<Path> files) {
        for (Path file : files) {
            processSingleFile(file);
        }
    }

    /**
     * Processes one file line-by-line.
     * Skips malformed lines and logs appropriate warnings.
     *
     * @param file path to the input file
     */
    private void processSingleFile(Path file) {
        System.out.println("Processing file: " + file);

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            int validCount = 0;
            int errorCount = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                UsageRecord record = parser.parseLine(line);
                if (record != null) {
                    aggregator.addUsage(record);
                    validCount++;
                } else {
                    errorCount++;
                    System.err.printf("Skipping invalid line %d in %s%n", lineNumber, file.getFileName());
                }
            }

            System.out.printf("Finished %s: %d valid, %d errors%n", file.getFileName(), validCount, errorCount);

        } catch (NoSuchFileException e) {
            System.err.println("File not found: " + file);
        } catch (IOException e) {
            System.err.println("Error reading file: " + file);
            e.printStackTrace(); // useful for developers
        } catch (Exception e) {
            System.err.println("Unexpected error while processing file: " + file);
            e.printStackTrace();
        }
    }
}
