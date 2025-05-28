package com.telecom.aggregator.model;

/**
 * Represents a single data usage record parsed from a telecom log file.
 * Holds all relevant fields parsed from a pipe-separated line.
 * Immutable once constructed.
 */
public class UsageRecord {

    private final String mobileNumber;
    private final String tower;
    private final int data4G;      // 4G usage in KB
    private final int data5G;      // 5G usage in KB
    private final boolean roaming; // True if the usage was during roaming

    /**
     * Constructs a UsageRecord instance.
     *
     * @param mobileNumber 10-digit mobile number
     * @param tower Tower ID from which the data was used
     * @param data4G Amount of 4G data used in KB
     * @param data5G Amount of 5G data used in KB
     * @param roaming Indicates whether the user was roaming
     */
    public UsageRecord(String mobileNumber, String tower, int data4G, int data5G, boolean roaming) {
        this.mobileNumber = mobileNumber;
        this.tower = tower;
        this.data4G = data4G;
        this.data5G = data5G;
        this.roaming = roaming;
    }

    // Getters — no setters to keep the class immutable

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getTower() {
        return tower;
    }

    public int getData4G() {
        return data4G;
    }

    public int getData5G() {
        return data5G;
    }

    public boolean isRoaming() {
        return roaming;
    }

    @Override
    public String toString() {
        return String.format("UsageRecord{mobile=%s, tower=%s, 4G=%d, 5G=%d, roaming=%s}",
                mobileNumber, tower, data4G, data5G, roaming);
    }
}
