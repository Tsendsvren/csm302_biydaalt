package com.os;

import java.util.*;

/** Гадаад сангүй, энгийн тест ажиллуулагч. Ажиллуулах: java -cp out com.os.AlgorithmTests */
public class AlgorithmTests {
    static int pass = 0, fail = 0;

    static void check(String id, String desc, boolean ok, String actual) {
        if (ok) pass++; else fail++;
        System.out.printf("[%s] %-4s %s%n       -> %s%n", ok ? "PASS" : "FAIL", id, desc, actual);
    }

    static SystemState textbook() {
        return new SystemState(5, 3, new int[]{3,3,2},
            new int[][]{{7,5,3},{3,2,2},{9,0,2},{2,2,2},{4,3,3}},
            new int[][]{{0,1,0},{2,0,0},{3,0,2},{2,1,1},{0,0,2}});
    }

    public static void main(String[] a) {
        System.out.println("=== Banker's Algorithm тестүүд ===");

        // T1 Сурах бичгийн Safe sequence
        List<Integer> seq = new ArrayList<>();
        boolean safe = BankerAlgorithm.checkSafety(textbook(), seq);
        check("T1a", "Сурах бичгийн эхний төлөв SAFE", safe, "safe=" + safe + " seq=" + seq);
        check("T1b", "Олдсон дараалал бие даасан шалгагчаар хүчинтэй (сурах бичгийн <P1,P3,P4,P0,P2>-аас өөр боловч зөв)",
              validSequence(textbook(), seq), seq.toString());
        check("T1c", "Сурах бичгийн <P1,P3,P4,P0,P2> дараалал мөн хүчинтэй",
              validSequence(textbook(), List.of(1,3,4,0,2)), "[1, 3, 4, 0, 2]");

        // T2 P1 (1,0,2) -> GRANTED
        SystemState s = textbook();
        var r = BankerAlgorithm.requestResources(s, 1, new int[]{1,0,2});
        check("T2", "P1 хүсэлт (1,0,2) зөвшөөрөгдөнө",
              r.status() == BankerAlgorithm.RequestResult.Status.GRANTED, r.status() + " " + r.safeSequence());
        check("T2b", "T2-ийн дараа Available=(2,3,0)",
              Arrays.equals(s.getAvailable(), new int[]{2,3,0}), Arrays.toString(s.getAvailable()));

        // T3/T4 Сурах бичгийн дарааллаар: P1 зөвшөөрөгдсөний дараах төлөвт
        s = textbook();
        BankerAlgorithm.requestResources(s, 1, new int[]{1,0,2});
        r = BankerAlgorithm.requestResources(s, 4, new int[]{3,3,0});
        check("T3", "P1-ийн дараа P4 хүсэлт (3,3,0): Available=(2,3,0) хүрэхгүй тул хүлээнэ",
              r.status() == BankerAlgorithm.RequestResult.Status.MUST_WAIT, r.status().toString());
        r = BankerAlgorithm.requestResources(s, 0, new int[]{0,2,0});
        check("T4", "P0 хүсэлт (0,2,0): нөөц байгаа ч UNSAFE тул татгалзана",
              r.status() == BankerAlgorithm.RequestResult.Status.UNSAFE, r.status().toString());
        check("T4b", "Татгалзсны дараа төлөв өөрчлөгдөөгүй (Available=(2,3,0))",
              Arrays.equals(s.getAvailable(), new int[]{2,3,0}), Arrays.toString(s.getAvailable()));
        check("T4c", "Эсрэг жишээ: ижил (0,2,0) хүсэлт эхний төлөвөөс GRANTED (төлөвөөс хамаарна)",
              BankerAlgorithm.requestResources(textbook(), 0, new int[]{0,2,0}).isGranted(), "initial state");

        // T5 Need-ээс хэтэрсэн
        r = BankerAlgorithm.requestResources(textbook(), 1, new int[]{9,0,0});
        check("T5", "Хүсэлт > Need → EXCEEDS_NEED",
              r.status() == BankerAlgorithm.RequestResult.Status.EXCEEDS_NEED, r.status().toString());

        // T6 Буруу оролт
        check("T6a", "Процессын ID хүрээнээс гарсан → INVALID",
              BankerAlgorithm.requestResources(textbook(), 9, new int[]{0,0,0}).status()
                  == BankerAlgorithm.RequestResult.Status.INVALID, "id=9");
        check("T6b", "Сөрөг хүсэлт → INVALID",
              BankerAlgorithm.requestResources(textbook(), 1, new int[]{-1,0,0}).status()
                  == BankerAlgorithm.RequestResult.Status.INVALID, "(-1,0,0)");
        check("T6c", "Буруу урттай вектор → INVALID",
              BankerAlgorithm.requestResources(textbook(), 1, new int[]{1,0}).status()
                  == BankerAlgorithm.RequestResult.Status.INVALID, "len=2");

        // T7 SystemState баталгаажуулалт
        boolean thrown = false; String msg = "";
        try { new SystemState(1,1,new int[]{1}, new int[][]{{2}}, new int[][]{{3}}); }
        catch (IllegalArgumentException e) { thrown = true; msg = e.getMessage(); }
        check("T7a", "Allocation > Max үед IllegalArgumentException", thrown, msg);
        thrown = false; msg = "";
        try { new SystemState(1,1,new int[]{-1}, new int[][]{{2}}, new int[][]{{1}}); }
        catch (IllegalArgumentException e) { thrown = true; msg = e.getMessage(); }
        check("T7b", "Сөрөг Available үед IllegalArgumentException", thrown, msg);

        // T8 Захын тохиолдол: 0 хүсэлт, 1x1 систем
        r = BankerAlgorithm.requestResources(textbook(), 1, new int[]{0,0,0});
        check("T8a", "Тэг хүсэлт GRANTED", r.isGranted(), r.status().toString());
        SystemState one = new SystemState(1,1,new int[]{0}, new int[][]{{0}}, new int[][]{{0}});
        seq = new ArrayList<>();
        check("T8b", "1 процесс, Need=0 → SAFE", BankerAlgorithm.checkSafety(one, seq), seq.toString());

        // T9 Ном 8.9(а) ба (б)
        int[][] mx = {{5,1,1,7},{3,2,1,1},{3,3,2,1},{4,6,1,2},{6,3,2,5}};
        int[][] al = {{3,0,1,4},{2,2,1,0},{3,1,2,1},{0,5,1,0},{4,2,1,2}};
        seq = new ArrayList<>();
        boolean a9 = BankerAlgorithm.checkSafety(new SystemState(5,4,new int[]{0,3,0,1}, mx, al), seq);
        check("T9a", "Дасгал 8.9(а) Available=(0,3,0,1): үр дүн", true, "safe=" + a9 + " seq=" + seq);
        seq = new ArrayList<>();
        boolean b9 = BankerAlgorithm.checkSafety(new SystemState(5,4,new int[]{1,0,0,2}, mx, al), seq);
        check("T9b", "Дасгал 8.9(б) Available=(1,0,0,2): үр дүн", true, "safe=" + b9 + " seq=" + seq);

        // T10 Бүх 10 сценарийн симуляцийн дүгнэлт
        System.out.println("\n--- Симуляцийн хөдөлгүүр: сценари бүрийн эцсийн алхам ---");
        List<Step> st2 = null;
        for (Scenario sc : ScenarioLibrary.all()) {
            List<Step> steps = SimulationEngine.run(sc);
            Step last = steps.get(steps.size() - 1);
            System.out.printf("%-62s steps=%3d  final=%-8s grants=%d waits=%d denied=%d done=%d/%d%n",
                sc.name(), steps.size(), last.kind, last.cGrants, last.cWaits, last.cDenied, last.cDone, sc.processes());
        }
        List<Scenario> all = ScenarioLibrary.all();
        check("T10a", "Сценари 3 (Banker унтраалттай) → DEADLOCK",
              lastKind(all.get(2)) == Step.Kind.DEADLOCK, lastKind(all.get(2)).toString());
        check("T10b", "Сценари 2 (Banker асаалттай) → DONE (түгжрэлгүй)",
              lastKind(all.get(1)) == Step.Kind.DONE, lastKind(all.get(1)).toString());
        check("T10c", "Сценари 4 → DONE, Сценари 5 → DEADLOCK",
              lastKind(all.get(3)) == Step.Kind.DONE && lastKind(all.get(4)) == Step.Kind.DEADLOCK,
              lastKind(all.get(3)) + " / " + lastKind(all.get(4)));

        engineTests();
        fuzz();

        System.out.printf("%n=== ДҮН: %d PASS, %d FAIL ===%n", pass, fail);
        System.exit(fail == 0 ? 0 : 1);
    }

    /** Бие даасан шалгагч: өгөгдсөн дараалал хүчинтэй Safe sequence мөн эсэх. */
    static boolean validSequence(SystemState st, List<Integer> order) {
        int n = st.getNumProcesses(), m = st.getNumResources();
        if (order.size() != n || new HashSet<>(order).size() != n) return false;
        int[] work = st.getAvailable();
        int[][] need = st.getNeed(), al = st.getAllocation();
        for (int p : order) {
            for (int j = 0; j < m; j++) if (need[p][j] > work[j]) return false;
            for (int j = 0; j < m; j++) work[j] += al[p][j];
        }
        return true;
    }

    /** Брут-форс: бүх permutation-аас хүчинтэй дараалал байна уу. */
    static boolean bruteSafe(SystemState st) {
        int n = st.getNumProcesses();
        return perm(st, new ArrayList<>(), new boolean[n]);
    }
    static boolean perm(SystemState st, List<Integer> cur, boolean[] used) {
        int n = st.getNumProcesses();
        if (cur.size() == n) return validSequence(st, cur);
        for (int i = 0; i < n; i++) if (!used[i]) {
            used[i] = true; cur.add(i);
            if (perm(st, cur, used)) return true;
            cur.remove(cur.size() - 1); used[i] = false;
        }
        return false;
    }

    static boolean hasKind(List<Step> l, Step.Kind k) { for (Step x : l) if (x.kind == k) return true; return false; }

    /** Номын сангийн гаднах, өөр өгөгдлөөр бүтээсэн Scenario дээр симуляцийн хөдөлгүүрийг шалгана. */
    static void engineTests() {
        // E1: Need-ээс хэтэрсэн хүсэлт -> ERROR алхам, cDenied=1
        Scenario e1 = new Scenario("E1", "", true, new int[]{2},
            new int[][]{{3},{3}}, new int[][]{{1},{1}}, List.of(new Scenario.Req(0, new int[]{5})));
        List<Step> r1 = SimulationEngine.run(e1);
        check("E1", "Өөр өгөгдөл: Need-ээс хэтэрсэн хүсэлт → ERROR алхам, Татгалзсан=1",
              hasKind(r1, Step.Kind.ERROR) && r1.get(r1.size()-1).cDenied >= 1,
              "ERROR=" + hasKind(r1, Step.Kind.ERROR) + " cDenied=" + r1.get(r1.size()-1).cDenied);

        // E2: Бүх Need=0 (бүх процесс аль хэдийн хэрэгцээгээ авсан) -> шууд DONE
        Scenario e2 = new Scenario("E2", "", true, new int[]{0,0},
            new int[][]{{1,1},{2,0}}, new int[][]{{1,1},{2,0}}, List.of());
        List<Step> r2 = SimulationEngine.run(e2);
        check("E2", "Захын тохиолдол: бүх Need=0 → DONE, 2/2 дууссан",
              r2.get(r2.size()-1).kind == Step.Kind.DONE && r2.get(r2.size()-1).cDone == 2,
              r2.get(r2.size()-1).kind + " done=" + r2.get(r2.size()-1).cDone);

        // E3: Banker асаалттай боловч эхний төлөв UNSAFE (Available=0, хүн ч дуусаж чадахгүй)
        Scenario e3 = new Scenario("E3", "", true, new int[]{0},
            new int[][]{{2},{2}}, new int[][]{{1},{1}}, List.of());
        List<Step> r3 = SimulationEngine.run(e3);
        check("E3", "Эхний төлөв UNSAFE ба бодит түгжрэл → DEADLOCK",
              r3.get(r3.size()-1).kind == Step.Kind.DEADLOCK, r3.get(r3.size()-1).kind.toString());

        // E4: Нэг нөөцтэй ганц процесс: Max > нийт нөөц (хэзээ ч дуусаж чадахгүй)
        Scenario e4 = new Scenario("E4", "", true, new int[]{1},
            new int[][]{{5}}, new int[][]{{1}}, List.of());
        List<Step> r4 = SimulationEngine.run(e4);
        check("E4", "Max > Total (нийт нөөцөөс их хэрэгцээ) → DEADLOCK гэж тодорхойлно",
              r4.get(r4.size()-1).kind == Step.Kind.DEADLOCK, r4.get(r4.size()-1).kind.toString());
    }

    static void fuzz() {
        Random rnd = new Random(42);
        int trials = 3000, mismatch = 0, safeCount = 0, badSeq = 0, invariantBreak = 0, grants = 0;
        for (int t = 0; t < trials; t++) {
            int n = 2 + rnd.nextInt(4), m = 1 + rnd.nextInt(3);
            int[][] mx = new int[n][m], al = new int[n][m];
            int[] av = new int[m];
            for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) {
                mx[i][j] = rnd.nextInt(6); al[i][j] = rnd.nextInt(mx[i][j] + 1);
            }
            for (int j = 0; j < m; j++) av[j] = rnd.nextInt(5);
            SystemState st = new SystemState(n, m, av, mx, al);
            List<Integer> seq = new ArrayList<>();
            boolean fast = BankerAlgorithm.checkSafety(st, seq);
            boolean brute = bruteSafe(st);
            if (fast != brute) mismatch++;
            if (fast) { safeCount++; if (!validSequence(st, seq)) badSeq++; }
            // Инвариант: зөвшөөрөгдсөн хүсэлтийн дараа төлөв SAFE хэвээр
            if (fast) {
                int p = rnd.nextInt(n);
                int[] req = new int[m];
                for (int j = 0; j < m; j++) req[j] = rnd.nextInt(3);
                var res = BankerAlgorithm.requestResources(st, p, req);
                if (res.isGranted()) {
                    grants++;
                    if (!BankerAlgorithm.checkSafety(st, new ArrayList<>())) invariantBreak++;
                }
            }
        }
        check("F1", "Fuzz " + trials + " санамсаргүй төлөв: checkSafety == брут-форс", mismatch == 0,
              "зөрүү=" + mismatch + ", SAFE төлөв=" + safeCount);
        check("F2", "Fuzz: олдсон Safe sequence бүр бие даасан шалгагчаар хүчинтэй", badSeq == 0, "буруу=" + badSeq);
        check("F3", "Fuzz: зөвшөөрөгдсөн хүсэлт бүрийн дараа систем SAFE хэвээр", invariantBreak == 0,
              "зөвшөөрөгдсөн=" + grants + ", зөрчил=" + invariantBreak);
    }

    static Step.Kind lastKind(Scenario sc) {
        List<Step> s = SimulationEngine.run(sc);
        return s.get(s.size() - 1).kind;
    }
}
