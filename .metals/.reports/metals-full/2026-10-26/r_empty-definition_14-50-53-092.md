error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java:_empty_/SystemState#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java
empty definition using pc, found symbol in pc: _empty_/SystemState#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1508
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/BankerAlgorithm.java
text:
```scala
package com.os;

import java.util.ArrayList;
import java.util.List;

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
    public static String requestResources1(SystemStat@@e state, int processId, int[] request) {
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

    public record RequestResult(Status status, String message, List<Integer> safeSequence) {
    public enum Status { GRANTED, MUST_WAIT, EXCEEDS_NEED, UNSAFE, INVALID }
}

public static RequestResult requestResources(SystemState state, int pid, int[] req) {
    int m = state.getNumResources();

    if (pid < 0 || pid >= state.getNumProcesses())
        return new RequestResult(com.os.BankerAlgorithm.RequestResult.Status.INVALID, "Процессын ID буруу байна.", List.of());
    if (req.length != m)
        return new RequestResult(Status.INVALID, m + " утга оруулна уу.", List.of());
    for (int r : req)
        if (r < 0) return new RequestResult(Status.INVALID, "Сөрөг утга оруулах боломжгүй.", List.of());

    for (int j = 0; j < m; j++)
        if (req[j] > state.getNeed()[pid][j])
            return new RequestResult(Status.EXCEEDS_NEED, "Хүсэлт Need-ээс хэтэрсэн.", List.of());
    for (int j = 0; j < m; j++)
        if (req[j] > state.getAvailable()[j])
            return new RequestResult(Status.MUST_WAIT, "P" + pid + " хүлээх ёстой.", List.of());

    SystemState trial = state.copy();           // хуулбар дээр туршина
    for (int j = 0; j < m; j++) {
        trial.getAvailable()[j]        -= req[j];
        trial.getAllocation()[pid][j]  += req[j];
        trial.getNeed()[pid][j]        -= req[j];
    }

    List<Integer> seq = new ArrayList<>();
    if (checkSafety(trial, seq)) {
        state.copyFrom(trial);                  // зөвхөн safe бол хэрэгжүүлнэ
        return new RequestResult(Status.GRANTED, "Зөвшөөрөгдөв.", seq);
    }
    return new RequestResult(Status.UNSAFE, "Unsafe болох тул цуцаллаа.", List.of());
}
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SystemState#