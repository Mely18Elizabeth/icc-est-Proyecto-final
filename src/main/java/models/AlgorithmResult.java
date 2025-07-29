package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlgorithmResult implements Serializable {

    private final String algorithmName;
    private final int pathLength;
    private final long executionTimeNanos;
    private final LocalDateTime timestamp;

    public AlgorithmResult(String algorithmName, int pathLength, long executionTimeNanos) {
        this.algorithmName = algorithmName;
        this.pathLength = pathLength;
        this.executionTimeNanos = executionTimeNanos;
        this.timestamp = LocalDateTime.now();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public int getPathLength() {
        return pathLength;
    }

    public long getExecutionTimeNanos() {
        return executionTimeNanos;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return algorithmName + "," + pathLength + "," + executionTimeNanos + "," + timestamp;
    }
}
