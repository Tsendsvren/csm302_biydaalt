package com.os;

import java.util.List;

public record Scenario(String name, String story, boolean bankerOn,
                       int[] available, int[][] max, int[][] allocation,
                       List<Req> requests) {

    public record Req(int process, int[] amount) {}

    public int processes() { return max.length; }

    public int resources() { return available.length; }

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