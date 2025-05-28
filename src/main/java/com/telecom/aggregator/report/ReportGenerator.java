package com.telecom.aggregator.report;

import com.telecom.aggregator.core.CostCalculator;
import com.telecom.aggregator.core.DataAggregator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

/**
 * Responsible for generating the final output report.
 * Outputs a file with mobile number-wise breakdown and cost.
 */
public class ReportGenerator {

    private final CostCalculator costCalculator;

    /**
     * Constructs a report generator using the given cost calculator.
     *
     * @param costCalculator for computing final costs per mobile number
     */
    public ReportGenerator(CostCalculator costCalculator) {
        this.costCalculator = costCalculator;
    }

    /**
     * Writes the final report to a file, summarizing usage and cost per user.
     *
     * @param outputFile     path to the output file
     * @param aggregatedData map of mobile number to usage data
     */
    public void generate(Path outputFile, Map<String, DataAggregator.AggregatedUsage> aggregatedData) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            // Write header line
            writer.write("Mobile Number|4G|5G|4G Roaming|5G Roaming|Cost");
            writer.newLine();

            for (Map.Entry<String, DataAggregator.AggregatedUsage> entry : aggregatedData.entrySet()) {
                String mobile = entry.getKey();
                DataAggregator.AggregatedUsage usage = entry.getValue();
                int cost = costCalculator.calculateCost(usage);

                // Format output
                String line = String.format("%s|%d|%d|%d|%d|%d",
                        mobile,
                        usage.data4GHome,
                        usage.data5GHome,
                        usage.data4GRoaming,
                        usage.data5GRoaming,
                        cost);

                writer.write(line);
                writer.newLine();
            }

            System.out.println("Report generated successfully: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error writing report to file: " + outputFile);
            e.printStackTrace(); // Developer output
        } catch (Exception e) {
            System.err.println("Unexpected error while generating report.");
            e.printStackTrace();
        }
    }
}
