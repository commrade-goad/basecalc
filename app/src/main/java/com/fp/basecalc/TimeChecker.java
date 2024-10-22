package com.fp.basecalc;

public class TimeChecker {
    private long startTime;

    public TimeChecker() {
        startTime = System.currentTimeMillis();
    }

    public boolean hasTwoSecondPassed() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - startTime) >= 2000; // 1000 milliseconds = 1 second
    }
    public void resetTimer() {
        startTime = System.currentTimeMillis();
    }
}
