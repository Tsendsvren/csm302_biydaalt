package com.os;


public class SystemState {
    private final int numProcesses;
    private final int numResources;
    private int[] available;
    private int[][] max;
    private int[][] allocation;
    private int[][] need;
    private boolean[] finished;

    public SystemState(int numProcesses, int numResources,
                       int[] available, int[][] max, int[][] allocation) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;
        validate(available, max, allocation);
        this.available = available.clone();
        this.max = deepCopy(max);
        this.allocation = deepCopy(allocation);
        this.need = new int[numProcesses][numResources];
        this.finished = new boolean[numProcesses];
        recalculateNeed();
    }

    private void validate(int[] available, int[][] max, int[][] allocation) {
        if (available.length != numResources)
            throw new IllegalArgumentException("Available-ийн урт нөөцийн тоотой тэнцүү байх ёстой.");
        if (max.length != numProcesses || allocation.length != numProcesses)
            throw new IllegalArgumentException("Max/Allocation мөрийн тоо процессын тоотой тэнцүү байх ёстой.");
        for (int j = 0; j < numResources; j++) {
            if (available[j] < 0)
                throw new IllegalArgumentException("Available[" + j + "] сөрөг байж болохгүй.");
        }
        for (int i = 0; i < numProcesses; i++) {
            if (max[i].length != numResources || allocation[i].length != numResources)
                throw new IllegalArgumentException("P" + i + " мөрийн багана буруу байна.");
            for (int j = 0; j < numResources; j++) {
                if (max[i][j] < 0 || allocation[i][j] < 0)
                    throw new IllegalArgumentException("P" + i + ", R" + j + ": сөрөг утга байж болохгүй.");
                if (allocation[i][j] > max[i][j])
                    throw new IllegalArgumentException(
                            "P" + i + ", R" + j + ": Allocation нь Max-аас их байж болохгүй.");
            }
        }
    }

    public void recalculateNeed() {
        for (int i = 0; i < numProcesses; i++) {
            for (int j = 0; j < numResources; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }

    /** Төлвийн бүрэн хуулбар. */
    public SystemState copy() {
        SystemState c = new SystemState(numProcesses, numResources, available, max, allocation);
        c.finished = finished.clone();
        return c;
    }

    /** Өөр төлвийн утгуудыг энэ төлөв рүү хуулна. */
    public void copyFrom(SystemState other) {
        if (other.numProcesses != numProcesses || other.numResources != numResources)
            throw new IllegalArgumentException("Хэмжээс таарахгүй байна.");
        this.available = other.available.clone();
        this.max = deepCopy(other.max);
        this.allocation = deepCopy(other.allocation);
        this.finished = other.finished.clone();
        recalculateNeed();
    }

    /** Хүсэлтийг шууд хуваарилна (шалгалтыг дуудагч хийсэн байх ёстой). */
    public void allocate(int processId, int[] request) {
        for (int j = 0; j < numResources; j++) {
            available[j] -= request[j];
            allocation[processId][j] += request[j];
            need[processId][j] -= request[j];
        }
    }

    /** Процесс ажлаа дуусгаж, бүх нөөцөө буцаана. */
    public void finish(int p) {
        for (int j = 0; j < numResources; j++) {
            available[j] += allocation[p][j];
            allocation[p][j] = 0;
            max[p][j] = 0;
            need[p][j] = 0;
        }
        finished[p] = true;
    }

    private static int[][] deepCopy(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) copy[i] = matrix[i].clone();
        return copy;
    }

    public int getNumProcesses() { return numProcesses; }
    public int getNumResources() { return numResources; }
    public int[] getAvailable() { return available.clone(); }
    public int[][] getMax() { return deepCopy(max); }
    public int[][] getAllocation() { return deepCopy(allocation); }
    public int[][] getNeed() { return deepCopy(need); }
    public boolean[] getFinished() { return finished.clone(); }
    public boolean isFinished(int p) { return finished[p]; }
}