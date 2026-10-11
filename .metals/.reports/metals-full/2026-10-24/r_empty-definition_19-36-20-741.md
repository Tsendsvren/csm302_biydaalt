error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Scenario.java:java/util/List#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Scenario.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 34
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Scenario.java
text:
```scala
package com.os;

import java.util.@@List;

/** Нэг симуляцийн сценари: анхны төлөв ба дарааллаар ирэх хүсэлтүүд. */
public record Scenario(String name, String story, boolean bankerOn,
                       int[] available, int[][] max, int[][] allocation,
                       List<Req> requests) {

    /** Процессын нөөцийн хүсэлт. */
    public record Req(int process, int[] amount) {}

    public int processes() { return max.length; }

    public int resources() { return available.length; }

    /** Системд нийт байгаа нөөц = Available + бүх Allocation. */
    public int[] total() {
        int[] t = available.clone();
        for (int[] row : allocation) {
            for (int j = 0; j < t.length; j++) t[j] += row[j];
        }
        return t;
    }

    public SystemState toState() {
        return new SystemState(processes(), resources(), available, max, allocation);
    }

    @Override
    public String toString() {
        return name;
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#