error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SystemState.java:_empty_/Array#clone().
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SystemState.java
empty definition using pc, found symbol in pc: _empty_/Array#clone().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3575
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SystemState.java
text:
```scala
package com.os;

/**
 * Системийн төлөв: Available, Max, Allocation, Need = Max - Allocation.
 * Getter-ууд дотоод массивын ХУУЛБАР буцаадаг тул гаднаас төлөвийг эвдэх боломжгүй.
 */
public class SystemState {
    private final int numProcesses;
    private final int numResources;
    private int[] available;
    private int[][] max;
    private int[][] allocation;
    private int[][] need;

    public SystemState(int numProcesses, int numResources,
                       int[] available, int[][] max, int[][] allocation) {
        this.numProcesses = numProcesses;
        this.numResources = numResources;
        validate(available, max, allocation);
        this.available = available.clone();
        this.max = deepCopy(max);
        this.allocation = deepCopy(allocation);
        this.need = new int[numProcesses][numResources];
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

    /** Төлвийн бүрэн хуулбар (алгоритмыг хуулбар дээр туршихад ашиглана). */
    public SystemState copy() {
        return new SystemState(numProcesses, numResources, available, max, allocation);
    }

    /** Өөр төлвийн утгуудыг энэ төлөв рүү хуулна. */
    public void copyFrom(SystemState other) {
        if (other.numProcesses != numProcesses || other.numResources != numResources)
            throw new IllegalArgumentException("Хэмжээс таарахгүй байна.");
        this.available = other.available.clone();
        this.max = deepCopy(other.max);
        this.allocation = deepCopy(other.allocation);
        recalculateNeed();
    }

    /** Хүсэлтийг шууд хуваарилна (шалгалтыг BankerAlgorithm хийсэн байх ёстой). */
    public void allocate(int processId, int[] request) {
        for (int j = 0; j < numResources; j++) {
            available[j] -= request[j];
            allocation[processId][j] += request[j];
            need[processId][j] -= request[j];
        }
    }

    private static int[][] deepCopy(int[][] matrix) {
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i].clone@@();
        }
        return copy;
    }

    public int getNumProcesses() { return numProcesses; }
    public int getNumResources() { return numResources; }
    public int[] getAvailable() { return available.clone(); }
    public int[][] getMax() { return deepCopy(max); }
    public int[][] getAllocation() { return deepCopy(allocation); }
    public int[][] getNeed() { return deepCopy(need); }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Array#clone().