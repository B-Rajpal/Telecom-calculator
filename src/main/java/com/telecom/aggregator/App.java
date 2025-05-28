package com.telecom.aggregator;

import com.telecom.aggregator.core.*;
import com.telecom.aggregator.parser.UsageRecordParser;
import com.telecom.aggregator.report.ReportGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point of the Telecom Aggregator Application.
 * Orchestrates reading input files, aggregating usage, computing costs,
 * and writing the final output report.
 */
public class App {

    public static void main(String[] args) {
        try {
            // === Configuration ===
            long usageThresholdInKB = 100000; // Apply 5% surcharge above this
            Path inputDir = Paths.get("input");
            Path outputPath = Paths.get("output", "final_report.txt");

            // === Generate Dummy Input Files ===
            com.telecom.aggregator.util.DummyFileGenerator.generate(inputDir);

            List<Path> inputFiles = List.of(
                Paths.get("input", "usage_file_1.txt"),
                Paths.get("input", "usage_file_2.txt")
            );

            // === Initialize Components ===
            UsageRecordParser parser = new UsageRecordParser();
            DataAggregator aggregator = new DataAggregator();
            CostCalculator calculator = new CostCalculator(usageThresholdInKB);
            FileProcessor fileProcessor = new FileProcessor(parser, aggregator);
            ReportGenerator reportGenerator = new ReportGenerator(calculator);

            // === Process Files ===
            fileProcessor.processFiles(inputFiles);

            // === Check Results ===
            if (aggregator.isEmpty()) {
                System.out.println("No valid records found. Report will not be generated.");
            } else {
                // Ensure output folder exists
                outputPath.getParent().toFile().mkdirs();

                // Generate final report
                reportGenerator.generate(outputPath, aggregator.getAggregatedData());
            }

        } catch (Exception e) {
            System.err.println("Fatal error in application execution.");
            e.printStackTrace();
        }
    }
}
