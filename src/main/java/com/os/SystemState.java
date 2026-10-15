package com.os;

public class SystemState {
    private int numProcesses;
    private int numResources;
    private int[] available;
    private int[][] max;
    private int[][] allocation;
    private int[][] need;

    public SystemState(int numProcesses, int numResources, int[] available, int[][] max, int[][] allocation) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;
        this.available = available.clone();
        this.max = deepCopy(max);
        this.allocation = deepCopy(allocation);
        this.need = new int[numProcesses][numResources];
        recalculateNeed();
    }

    public void recalculateNeed() {
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                this.need[i][j] = this.max[i][j] - this.allocation[i][j];
            }
        }
    }

    private int[][] deepCopy(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    public SystemState copy() {
    return new SystemState(numProcesses, numResources, available, max, allocation);
}

public void copyFrom(SystemState other) {
    this.available = other.available.clone();
    this.allocation = deepCopy(other.allocation);
    this.max = deepCopy(other.max);
    recalculateNeed();
}

// Constructor дотор:
// for i,j: if (allocation[i][j] > max[i][j]) throw new IllegalArgumentException(...)

    // Getters and Setters
    public int getNumProcesses() { return numProcesses; }
    public int getNumResources() { return numResources; }
    public int[] getAvailable() { return available; }
    public int[][] getMax() { return max; }
    public int[][] getAllocation() { return allocation; }
    public int[][] getNeed() { return need; }

    public void setAvailable(int[] available) { this.available = available.clone(); }
    public void setAllocation(int[][] allocation) { this.allocation = deepCopy(allocation); recalculateNeed(); }
    public void setMax(int[][] max) { this.max = deepCopy(max); recalculateNeed(); }
}