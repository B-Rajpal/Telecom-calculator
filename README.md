# Telecom Data Aggregator

This Java 21 project processes telecom usage logs to generate aggregated reports per mobile number. It handles large volumes of data, applies rate-based cost computations, and produces structured output with robust error handling and modular design.

### 🔗 GitHub Repository

**Clone or fork the repo from:** [https://github.com/B-Rajpal/Telecom-calculator.git](https://github.com/B-Rajpal/Telecom-calculator.git)

---

## 📁 Project Structure

```
Telecom-aggregator/
├── pom.xml
├── .gitignore
├── README.md
├── input/                      # Sample input files
├── output/                     # Processed reports 
├── test_files/                 # Temporary test files and test report
├── src/
│   ├── main/java/
│   │   └── com/telecom/aggregator/
│   │       ├── App.java        # Entry point
│   │       ├── model/UsageRecord.java
│   │       ├── parser/UsageRecordParser.java
│   │       ├── core/DataAggregator.java
│   │       ├── core/FileProcessor.java
│   │       └── core/CostCalculator.java
│   ├── test/java/
        └── com/telecom/aggregator/core/
            └── AggregatorAndFileProcessorTest.java  # Combined JUnit test class
```

---

## ⚙️ Features

* Parses and aggregates 4G/5G usage data per user
* Distinguishes roaming vs. non-roaming charges
* Configurable cost computation (threshold penalty, roaming premium)
* Skips malformed lines gracefully
* Outputs result to terminal and to `output/report.txt`
* Test cases with test output saved in `test_files/test-report.txt`

---

## 🖥️ How to Run

### 1. Clone and Open in VSCode or Eclipse

```bash
git clone https://github.com/B-Rajpal/Telecom-aggregator.git
cd Telecom-aggregator
```

### 2. Import as Maven Project (Eclipse)

* File → Import → Existing Maven Projects
* Browse to the cloned folder
* Build project

### 3. Run the Application

Run `App.java` as a Java application. It will:

* Create dummy files with usage data
* Parse and aggregate those files
* Output the results to terminal and to `output/report.txt`

---

## 📥 Sample Input Format

Each line in the input file (e.g., `input/usage_file_1.txt`) is:

```
MobileNumber|Tower|4G|5G|Roaming
```

---

## 📤 Sample Output Format

```
Mobile Number|4G|5G|4G Roaming|5G Roaming|Cost
```

Saved to: `output/report.txt`

---

## ✅ Test Cases Covered

**Test Class:** `AggregatorAndFileProcessorTest`

| Test Case                       | Description                                                           | Result |
| ------------------------------- | --------------------------------------------------------------------- | --------------- |
| Aggregator Initial State        | Ensure aggregator is empty before any usage is added.                 | ✅ PASSED        |
| Single 4G Home Usage            | Validate that non-roaming 4G usage is recorded correctly.             | ✅ PASSED        |
| Single 5G Roaming Usage         | Check if 5G usage in roaming is recorded properly.                    | ✅ PASSED        |
| Multiple Records Same Number    | Test cumulative addition for same user across roaming and home usage. | ✅ PASSED        |
| Multiple Mobile Numbers         | Ensure distinct mobile numbers are tracked separately.                | ✅ PASSED        |
| Null Usage Record               | Verify null records are safely ignored without error.                 | ✅ PASSED        |
| File Processing with Valid Data | Test complete data processing pipeline from file to aggregation.      | ✅ PASSED        |
| File with Malformed Lines       | Ensure invalid records are skipped without crashing.                  | ✅ PASSED        |
| Non-existent File Handling      | Confirm system handles missing files gracefully.                      | ✅ PASSED        |

### 📁 Test Files and Output Directory

All temporary test input files and the generated test report (`test-report.txt`) are saved in the `test_files/` directory during test execution.

---

## 🙋‍♂️ Author

**Rajpal B**
GitHub: [B-Rajpal](https://github.com/B-Rajpal)

---

Let me know if you find bugs or have suggestions! 🙌
