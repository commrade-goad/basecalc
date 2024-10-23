package com.fp.basecalc;

public class CalculationEntry {
    private long timestamp;
    private String calculation;

    public CalculationEntry(long timestamp, String calculation) {
        this.timestamp = timestamp;
        this.calculation = calculation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCalculation() {
        return calculation;
    }
}
