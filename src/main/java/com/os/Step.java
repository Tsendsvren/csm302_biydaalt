package com.os;

import java.util.List;

public class Step {
    public enum Kind { INTRO, INFO, REQUEST, CHECK, SAFETY, DETECT, GRANT, WAIT, DENY, ERROR, FINISH, DEADLOCK, DONE }

    public enum Status { IDLE, REQUESTING, CHECKING, WAITING, FINISHED, DEADLOCKED }

    public enum Sys { UNKNOWN, SAFE, UNSAFE, DEADLOCK }

    public enum Code { NONE, REQUEST, SAFETY, DETECT }

    public record Check(String label, Boolean ok) {}

    public Kind kind;
    public String title;
    public String text;
    public Code code = Code.NONE;
    public int codeLine = -1;
    public List<Check> checks = List.of();

    public int[] total;
    public int[] available;
    public int[][] allocation;
    public int[][] max;
    public int[][] need;
    public boolean[] finished;
    public Status[] status;
    public int active = -1;
    public int[] request;
    public int requester = -1;
    public int[] work;
    public boolean[] finishFlags;
    public List<Integer> safeSeq = List.of();
    public List<Scenario.Req> waiting = List.of();
    public Sys sys = Sys.UNKNOWN;
    public boolean bankerOn;

    public int cChecks, cGrants, cWaits, cDenied, cDone;
}