error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Step.java:java/util/List#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Step.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 34
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/Step.java
text:
```scala
package com.os;

import java.util.@@List;

/** Симуляцийн нэг алхам: тухайн мөчийн бүх төлвийн хуулбар ба энгийн тайлбар. */
public class Step {
    public enum Kind { INTRO, INFO, REQUEST, CHECK, SAFETY, DETECT, GRANT, WAIT, DENY, ERROR, FINISH, DEADLOCK, DONE }

    public enum Status { IDLE, REQUESTING, CHECKING, WAITING, FINISHED, DEADLOCKED }

    public enum Sys { UNKNOWN, SAFE, UNSAFE, DEADLOCK }

    public enum Code { NONE, REQUEST, SAFETY, DETECT }

    /** Шалгалтын мөр: ok=true ✓, false ✗, null хүлээгдэж байна. */
    public record Check(String label, Boolean ok) {}

    public Kind kind;
    public String title;
    public String text;
    public Code code = Code.NONE;
    public int codeLine = -1;
    public List<Check> checks = List.of();

    // Төлвийн хуулбар
    public int[] total;
    public int[] available;
    public int[][] allocation;
    public int[][] max;
    public int[][] need;
    public boolean[] finished;
    public Status[] status;
    public int active = -1;
    public int[] request;                 // одоо хүсэж буй вектор (null байж болно)
    public int requester = -1;            // хүсэлт гаргагч процесс
    public int[] work;                    // Safety/Detection үеийн Work (null байж болно)
    public boolean[] finishFlags;         // Safety/Detection үеийн Finish (null байж болно)
    public List<Integer> safeSeq = List.of();
    public List<Scenario.Req> waiting = List.of();
    public Sys sys = Sys.UNKNOWN;
    public boolean bankerOn;

    // Тоолуурууд
    public int cChecks, cGrants, cWaits, cDenied, cDone;
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#