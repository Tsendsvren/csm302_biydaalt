package com.os;

import java.util.ArrayList;
import java.util.List;

public class BankerAlgorithm {
    public record RequestResult(Status status, String message, List<Integer> safeSequence) {
        public enum Status { GRANTED, MUST_WAIT, EXCEEDS_NEED, UNSAFE, INVALID }

        public boolean isGranted() {
            return status == Status.GRANTED;
        }
    }
    public static boolean leq(int[] a, int[] b) {
        for (int j = 0; j < a.length; j++) {
            if (a[j] > b[j]) return false;
        }
        return true;
    }

    public static boolean checkSafety(SystemState state, List<Integer> safeSequence) {
        int n = state.getNumProcesses();
        int m = state.getNumResources();

        int[] work = state.getAvailable();
        int[][] need = state.getNeed();
        int[][] allocation = state.getAllocation();
        boolean[] finish = state.getFinished();
        safeSequence.clear();

        while (true) {
            int found = -1;
            for (int p = 0; p < n; p++) {
                if (!finish[p] && leq(need[p], work)) {
                    found = p;
                    break;
                }
            }
            if (found < 0) break;
            for (int k = 0; k < m; k++) work[k] += allocation[found][k];
            finish[found] = true;
            safeSequence.add(found);
        }

        for (boolean f : finish) {
            if (!f) return false; // Unsafe
        }
        return true; // Safe
    }

    public static RequestResult requestResources(SystemState state, int processId, int[] request) {
        int m = state.getNumResources();

        if (processId < 0 || processId >= state.getNumProcesses() || state.isFinished(processId))
            return new RequestResult(RequestResult.Status.INVALID,
                    "Алдаа: Процессын ID буруу эсвэл процесс аль хэдийн дууссан.", List.of());
        if (request.length != m)
            return new RequestResult(RequestResult.Status.INVALID,
                    "Алдаа: " + m + " утга оруулна уу.", List.of());
        for (int r : request) {
            if (r < 0)
                return new RequestResult(RequestResult.Status.INVALID,
                        "Алдаа: Сөрөг утга оруулах боломжгүй.", List.of());
        }

        if (!leq(request, state.getNeed()[processId]))
            return new RequestResult(RequestResult.Status.EXCEEDS_NEED,
                    "Алдаа: Хүсэлт нь P" + processId + " процессын дээд хэрэгцээнээс (Need) хэтэрсэн.", List.of());

        if (!leq(request, state.getAvailable()))
            return new RequestResult(RequestResult.Status.MUST_WAIT,
                    "Анхаар: Бэлэн нөөц хүрэлцэхгүй тул P" + processId + " хүлээх ёстой.", List.of());

        SystemState trial = state.copy();
        trial.allocate(processId, request);

        List<Integer> seq = new ArrayList<>();
        if (checkSafety(trial, seq)) {
            state.copyFrom(trial);
            return new RequestResult(RequestResult.Status.GRANTED,
                    "Амжилттай: Хүсэлт зөвшөөрөгдөв. Safe Sequence: " + seq, seq);
        }
        return new RequestResult(RequestResult.Status.UNSAFE,
                "Цуцлагдсан: Хүсэлтийг биелүүлбэл систем Unsafe төлөвт орно.", List.of());
    }
}