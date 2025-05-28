package com.telecom.aggregator.core;

import com.telecom.aggregator.model.UsageRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregates usage records per mobile number and by data category.
 * Categories tracked:
 * - 4G Home
 * - 5G Home
 * - 4G Roaming
 * - 5G Roaming
 */
public class DataAggregator {

    /**
     * Internal data holder representing cumulative usage by category for a user.
     */
    public static class AggregatedUsage {
        public long data4GHome = 0;
        public long data5GHome = 0;
        public long data4GRoaming = 0;
        public long data5GRoaming = 0;

        @Override
        public String toString() {
            return "4G=" + data4GHome + ", 5G=" + data5GHome +
                   ", 4G Roaming=" + data4GRoaming + ", 5G Roaming=" + data5GRoaming;
        }
    }

    // Map to hold aggregate per mobile number
    private final Map<String, AggregatedUsage> userUsageMap = new HashMap<>();

    /**
     * Adds a single usage record to the aggregation store.
     *
     * @param record the usage record to be accumulated
     */
    public void addUsage(UsageRecord record) {
        if (record == null) return;

        // Fetch or create aggregation object
        AggregatedUsage usage = userUsageMap.computeIfAbsent(
                record.getMobileNumber(), k -> new AggregatedUsage());

        // Classify based on roaming
        if (record.isRoaming()) {
            usage.data4GRoaming += record.getData4G();
            usage.data5GRoaming += record.getData5G();
        } else {
            usage.data4GHome += record.getData4G();
            usage.data5GHome += record.getData5G();
        }
    }

    /**
     * Returns the complete aggregated data map.
     *
     * @return map from mobile number to usage summary
     */
    public Map<String, AggregatedUsage> getAggregatedData() {
        return userUsageMap;
    }

    /**
     * Returns true if any records were aggregated.
     */
    public boolean isEmpty() {
        return userUsageMap.isEmpty();
    }
}
