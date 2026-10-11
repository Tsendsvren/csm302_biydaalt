error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java:java/util/List#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 72
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java
text:
```scala
package main.java.com.os;

import java.util.ArrayList;
import java.util.@@List;

public class BankerAlgorithm {
    
    // Safety Algorithm: Систем аюулгүй төлөвт байгааг шалгах
    public static boolean checkSafety(SystemState state, List<Integer> safeSequence) {
        int n = state.getNumProcesses();
        int m = state.getNumResources();
        
        int[] work = state.getAvailable().clone();
        boolean[] finish = new boolean[n];
        safeSequence.clear();

        int count = 0;
        while (count < n) {
            boolean found = false;
            for (int p = 0; p < n; p++) {
                if (!finish[p]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (state.getNeed()[p][j] > work[j])
                            break;
                    }

                    if (j == m) {
                        for (int k = 0; k < m; k++) {
                            work[k] += state.getAllocation()[p][k];
                        }
                        safeSequence.add(p);
                        finish[p] = true;
                        found = true;
                        count++;
                    }
                }
            }

            if (!found) {
                return false; // Unsafe state (Deadlock үүсэх эрсдэлтэй)
            }
        }
        return true; // Safe state
    }

    // Resource Request Algorithm: Шинэ хүсэлтийг шалгах ба хуваарилах
    public static String requestResources(SystemState state, int processId, int[] request) {
        int m = state.getNumResources();

        // 1. Request <= Need шалгах
        for (int j = 0; j < m; j++) {
            if (request[j] > state.getNeed()[processId][j]) {
                return "Алдаа: Хүсэлт нь тухайн процессын дээд хэрэгцээнээс (Need) хэтэрсэн байна!";
            }
        }

        // 2. Request <= Available шалгах
        for (int j = 0; j < m; j++) {
            if (request[j] > state.getAvailable()[j]) {
                return "Анхаар: Системд бэлэн нөөц хүрэлцэхгүй тул P" + processId + " хүлээх ёстой.";
            }
        }

        // 3. Түр хуваарилах (Pretend Allocation)
        for (int j = 0; j < m; j++) {
            state.getAvailable()[j] -= request[j];
            state.getAllocation()[processId][j] += request[j];
            state.getNeed()[processId][j] -= request[j];
        }

        // 4. Safe State шалгах
        List<Integer> safeSeq = new ArrayList<>();
        boolean safe = checkSafety(state, safeSeq);

        if (safe) {
            return "Амжилттай: Хүсэлт зөвшөөрөгдөв. Систем Safe State-д байна. Safe Sequence: " + safeSeq;
        } else {
            // Unsafe бол буцаах (Rollback)
            for (int j = 0; j < m; j++) {
                state.getAvailable()[j] += request[j];
                state.getAllocation()[processId][j] -= request[j];
                state.getNeed()[processId][j] += request[j];
            }
            return "Цуцлагдсан: Хүсэлтийг биелүүлбэл систем Unsafe төлөвт орох тул цуцаллаа (Rollback).";
        }
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#