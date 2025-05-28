package com.telecom.aggregator.core;

/**
 * Calculates the cost for each user's total data usage, applying:
 * - Standard rates
 * - Roaming surcharges
 * - Threshold-based 5% surcharge
 */
public class CostCalculator {

    // === Base Rates (can be later externalized to config) ===
    private static final double BASE_RATE_4G = 0.01;  // per KB
    private static final double BASE_RATE_5G = 0.02;

    private static final double ROAMING_MULTIPLIER_4G = 1.10;  // 10% extra
    private static final double ROAMING_MULTIPLIER_5G = 1.15;  // 15% extra

    private static final double SURCHARGE_PERCENT = 0.05;       // 5% extra
    private final long thresholdInKB; // KB limit above which surcharge applies

    /**
     * Constructs a CostCalculator with a specific threshold.
     *
     * @param thresholdInKB total usage above which surcharge applies
     */
    public CostCalculator(long thresholdInKB) {
        this.thresholdInKB = thresholdInKB;
    }

    /**
     * Calculates the final cost for a user’s aggregated usage.
     *
     * @param usage the usage object with all categorized KB usage
     * @return the rounded total cost in integer units
     */
    public int calculateCost(DataAggregator.AggregatedUsage usage) {
        if (usage == null) {
            return 0;
        }

        // === Calculate normal costs ===
        double cost =
                (usage.data4GHome * BASE_RATE_4G) +
                (usage.data5GHome * BASE_RATE_5G) +
                (usage.data4GRoaming * BASE_RATE_4G * ROAMING_MULTIPLIER_4G) +
                (usage.data5GRoaming * BASE_RATE_5G * ROAMING_MULTIPLIER_5G);

        // === Apply 5% surcharge if usage crosses threshold ===
        long totalUsage = usage.data4GHome + usage.data5GHome + usage.data4GRoaming + usage.data5GRoaming;
        if (totalUsage > thresholdInKB) {
            cost *= (1 + SURCHARGE_PERCENT);
        }

        return (int) Math.round(cost);
    }
}
