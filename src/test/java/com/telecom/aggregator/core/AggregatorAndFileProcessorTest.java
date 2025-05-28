package com.telecom.aggregator.core;

import com.telecom.aggregator.model.UsageRecord;
import com.telecom.aggregator.parser.UsageRecordParser;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Combined tests for DataAggregator and FileProcessor classes.
 * - Includes validation of aggregation logic
 * - Tests end-to-end flow from file to aggregation
 * - Stores test report in a dedicated output directory
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregatorAndFileProcessorTest {

    private static final Path TEST_DIR = Paths.get("test_files"); // Persistent directory
    private static final Path REPORT_FILE = TEST_DIR.resolve("test_report.txt"); // Output report file

    private DataAggregator aggregator;
    private UsageRecordParser parser;
    private FileProcessor fileProcessor;

    private final List<String> reportLines = new ArrayList<>(); // To hold the test report entries

    // ==================== SETUP & CLEANUP ====================

    @BeforeAll
    void setupAll() throws IOException {
        // Ensure test_files directory exists
        if (!Files.exists(TEST_DIR)) {
            Files.createDirectories(TEST_DIR);
        }
    }

    @AfterAll
    void writeReportToFile() throws IOException {
        // Write final report summary to test_report.txt
        try (BufferedWriter writer = Files.newBufferedWriter(REPORT_FILE)) {
            writer.write("=== Test Execution Report ===\n\n");
            for (String entry : reportLines) {
                writer.write(entry + "\n");
            }
            writer.write("\n=== End of Report ===\n");
        }
        System.out.println("Test report written to: " + REPORT_FILE.toAbsolutePath());
    }

    @BeforeEach
    void setupEach() {
        aggregator = new DataAggregator();
        parser = new UsageRecordParser();
        fileProcessor = new FileProcessor(parser, aggregator);
    }

    // ==================== HELPER METHODS ====================

    private void logResult(String testName, String description, boolean passed) {
        reportLines.add(String.format("Test: %s\nDescription: %s\nResult: %s\n",
                testName, description, passed ? "PASSED" : "FAILED"));
    }

    private Path createTestFile(String filename, List<String> lines) throws IOException {
        Path file = TEST_DIR.resolve(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return file;
    }

    // ==================== TEST CASES ====================

    @Test
    void testAggregateSingle5GRoaming() {
        String testName = "Single 5G Roaming Usage";
        String desc = "Check if 5G usage in roaming is recorded properly.";
        try {
            UsageRecord record = new UsageRecord("9000600601", "TowerB", 0, 2000, true);
            aggregator.addUsage(record);
            var usage = aggregator.getAggregatedData().get("9000600601");

            assertEquals(2000, usage.data5GRoaming);
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testMalformedFileLines() throws IOException {
        String testName = "File with Malformed Lines";
        String desc = "Ensure invalid records are skipped without crashing.";
        List<String> lines = List.of(
                "9000600600|InAir1234|1000|0|No",
                "invalid|line|with|missing|fields",
                "9000600601|TowerX|abc|1500|Yes",  // Invalid numeric
                "9000600602|TowerY|500|500|Maybe"  // Invalid roaming flag
        );
        Path file = createTestFile("malformed.txt", lines);
        try {
            fileProcessor.processFiles(List.of(file));
            assertEquals(1, aggregator.getAggregatedData().size());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testNonExistentFile() {
        String testName = "Non-existent File Handling";
        String desc = "Confirm system handles missing files gracefully.";
        Path nonexistent = TEST_DIR.resolve("does_not_exist.txt");
        try {
            fileProcessor.processFiles(List.of(nonexistent));
            assertTrue(aggregator.isEmpty());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testAggregatorInitialState() {
        String testName = "Aggregator Initial State";
        String desc = "Ensure aggregator is empty before any usage is added.";
        try {
            assertTrue(aggregator.isEmpty());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testMultipleMobileNumbers() {
        String testName = "Multiple Mobile Numbers";
        String desc = "Ensure distinct mobile numbers are tracked separately.";
        try {
            aggregator.addUsage(new UsageRecord("9000600603", "TowerD", 100, 200, false));
            aggregator.addUsage(new UsageRecord("9000600604", "TowerE", 400, 0, true));
            assertEquals(2, aggregator.getAggregatedData().size());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testFileProcessingValid() throws IOException {
        String testName = "File Processing with Valid Data";
        String desc = "Test complete data processing pipeline from file to aggregation.";
        List<String> lines1 = List.of(
                "9000600600|InAir1234|1000|0|No",
                "9000600601|TowerX|0|1500|Yes"
        );
        List<String> lines2 = List.of(
                "9000600600|TowerY|500|0|No",
                "9000600602|TowerZ|0|3000|No"
        );

        Path file1 = createTestFile("file1.txt", lines1);
        Path file2 = createTestFile("file2.txt", lines2);
        try {
            fileProcessor.processFiles(List.of(file1, file2));
            assertEquals(3, aggregator.getAggregatedData().size());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testSingle4GHomeUsage() {
        String testName = "Single 4G Home Usage";
        String desc = "Validate that non-roaming 4G usage is recorded correctly.";
        try {
            UsageRecord record = new UsageRecord("9000600600", "TowerA", 1000, 0, false);
            aggregator.addUsage(record);
            var usage = aggregator.getAggregatedData().get("9000600600");

            assertEquals(1000, usage.data4GHome);
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testNullUsageRecord() {
        String testName = "Null Usage Record";
        String desc = "Verify null records are safely ignored without error.";
        try {
            aggregator.addUsage(null);
            assertTrue(aggregator.isEmpty());
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }

    @Test
    void testMultipleRecordsSameNumber() {
        String testName = "Multiple Records Same Number";
        String desc = "Test cumulative addition for same user across roaming and home usage.";
        try {
            aggregator.addUsage(new UsageRecord("9000600602", "TowerC", 500, 0, false));
            aggregator.addUsage(new UsageRecord("9000600602", "TowerC", 0, 1500, true));
            aggregator.addUsage(new UsageRecord("9000600602", "TowerC", 300, 700, false));

            var usage = aggregator.getAggregatedData().get("9000600602");
            assertEquals(800, usage.data4GHome);
            assertEquals(700, usage.data5GHome);
            assertEquals(1500, usage.data5GRoaming);
            logResult(testName, desc, true);
        } catch (Exception e) {
            logResult(testName, desc, false);
            fail(e);
        }
    }
}
