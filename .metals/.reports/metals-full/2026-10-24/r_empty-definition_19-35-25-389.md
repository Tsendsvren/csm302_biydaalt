error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulationEngine.java:com/os/Step/Check#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulationEngine.java
empty definition using pc, found symbol in pc: com/os/Step/Check#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 36
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulationEngine.java
text:
```scala
package com.os;

import com.os.Step.@@Check;
import com.os.Step.Code;
import com.os.Step.Kind;
import com.os.Step.Status;
import com.os.Step.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.os.BankerAlgorithm.leq;

/**
 * Сценарийг эхнээс нь дуустал "ажиллуулж", алхам бүрийг энгийн үгээр тайлбарласан
 * {@link Step}-үүдийн жагсаалт болгон гаргана. Алгоритмууд нь Silberschatz,
 * "Operating System Concepts" Chapter 8-ийн дагуу:
 * Safety (8.6.3.1), Resource-Request (8.6.3.2), Deadlock Detection (8.7.2).
 */
public class SimulationEngine {
    private enum Res { GRANTED, WAIT, DENIED, ERROR }

    private final Scenario sc;
    private final SystemState st;
    private final int n, m;
    private final int[] total;
    private final List<Step> steps = new ArrayList<>();
    private final Status[] status;
    private final List<Scenario.Req> waiting = new ArrayList<>();

    private int active = -1;
    private int[] reqVec;
    private int reqProc = -1;
    private int[] work;
    private boolean[] finishFlags;
    private boolean[] lastFlags;
    private List<Integer> seq = new ArrayList<>();
    private List<Check> curChecks = new ArrayList<>();
    private Sys sys = Sys.UNKNOWN;
    private int cChecks, cGrants, cWaits, cDenied, cDone;

    private SimulationEngine(Scenario s) {
        sc = s;
        st = s.toState();
        n = s.processes();
        m = s.resources();
        total = s.total();
        status = new Status[n];
        Arrays.fill(status, Status.IDLE);
    }

    public static List<Step> run(Scenario s) {
        return new SimulationEngine(s).build();
    }

    // ------------------------------------------------------------------ build

    private List<Step> build() {
        String mode = sc.bankerOn()
                ? "Banker's Algorithm АСААЛТТАЙ: систем нөөц өгөхөөсөө өмнө «өгвөл аюулгүй юу?» гэж шалгана."
                : "Banker's Algorithm УНТРААТАЙ: систем нөөц байвал л өгнө. Дараа нь түгжрэл (deadlock) болсон эсэхийг Detection алгоритмаар шалгана.";
        emit(Kind.INTRO, sc.name(), sc.story() + "\n\n" + mode, Code.NONE, -1, null);

        if (sc.bankerOn()) {
            emit(Kind.INFO, "Эхлээд системийн одоогийн төлөвийг шалгая",
                    "Банкир зээл өгөхөөсөө өмнө «бүх үйлчлүүлэгч эцэст нь хэрэгцээгээ авч, ажлаа дуусгаж чадах уу?» гэж бодно. "
                            + "Үүнийг Safety Algorithm гэнэ.", Code.NONE, -1, null);
            runSafety(false);
        }

        if (!sc.requests().isEmpty()) {
            emit(Kind.INFO, "Процессууд нөөц хүсч эхэллээ",
                    "Одоо процессууд нэг нэгээрээ нөөц хүснэ. Хүсэлт бүрийг систем шалгаад зөвшөөрөх эсвэл хүлээлгэнэ.",
                    Code.NONE, -1, null);
        }
        for (Scenario.Req r : sc.requests()) {
            if (!st.isFinished(r.process())) processRequest(r.process(), r.amount(), true);
        }

        phase2();
        return steps;
    }

    private void phase2() {
        active = -1;
        reqVec = null;
        emit(Kind.INFO, "Хүсэлтүүд дууслаа. Одоо үлдсэн ажлуудыг дуусгая",
                "Процесс бүр ажлаа дуусгахын тулд өөрийн үлдсэн хэрэгцээг (Need) бүрэн авах ёстой. "
                        + "Бүгд авч чадах уу, эсвэл түгжрэл гарах уу гэдгийг харъя.", Code.NONE, -1, null);
        retryWaiting();

        if (!allFinished()) {
            List<Integer> sq = new ArrayList<>();
            boolean safe = BankerAlgorithm.checkSafety(st, sq);
            if (!sc.bankerOn() || !safe) {
                emit(Kind.INFO, "Түгжрэл (deadlock) байгаа эсэхийг шалгая",
                        "Үлдсэн бүх процесс хэрэгцээгээ (Need) нэгэн зэрэг хүсэх гэж үзье. "
                                + "Deadlock Detection алгоритм аль нь ажлаа дуусгаж чадахыг, аль нь чадахгүйг хэлнэ.",
                        Code.NONE, -1, null);
                boolean ok = runSafety(true);
                if (!ok) {
                    finishDeadlock();
                    return;
                }
                sys = Sys.SAFE;
                emit(Kind.INFO, "Түгжрэл байхгүй",
                        "Бүх процесс ээлж дараалан ажлаа дуусгаж чадна. Одоо тэднийг жинхэнээсээ ажиллуулъя.",
                        Code.NONE, -1, null);
            }

            sq = new ArrayList<>();
            BankerAlgorithm.checkSafety(st, sq);
            seq = new ArrayList<>(sq);
            emit(Kind.INFO, "Аюулгүй дараалал: " + seqText(sq),
                    "Энэ дарааллаар явуулбал процесс бүр хэрэгтэй нөөцөө авч, ажлаа дуусгаад буцаана. "
                            + "Ингэж ээлж дараалан явуулна.", Code.NONE, -1, null);

            while (!allFinished()) {
                sq = new ArrayList<>();
                if (!BankerAlgorithm.checkSafety(st, sq)) {
                    boolean ok = runSafety(true);
                    if (!ok) {
                        finishDeadlock();
                        return;
                    }
                    break;
                }
                int p = sq.get(0);
                waiting.removeIf(w -> w.process() == p);
                int[] need = st.getNeed()[p];
                active = p;
                if (isZero(need)) {
                    finishProcess(p);
                } else {
                    emit(Kind.INFO, "Ээлж " + pn(p) + "-д",
                            "Аюулгүй дарааллын дагуу одоо " + pn(p) + "-ийн ээлж. Тэр үлдсэн хэрэгцээгээ бүрэн хүснэ.",
                            Code.NONE, -1, null);
                    processRequest(p, need, false);
                }
            }
        }

        sys = Sys.SAFE;
        active = -1;
        reqVec = null;
        work = null;
        finishFlags = null;
        emit(Kind.DONE, "Бүх процесс амжилттай дууслаа",
                "Түгжрэл (deadlock) гарсангүй. Нийт " + n + " процессоос бүгд ажлаа дуусгаж, нөөцөө буцаалаа. "
                        + "Нэмэлтээр: ямар хүсэлт зөвшөөрөгдөж, ямар нь хүлээсэн, ямар нь татгалзагдсаныг доорх тоолуураас харна уу.",
                Code.NONE, -1, null);
    }

    // --------------------------------------------------------------- request

    private Res processRequest(int p, int[] req, boolean verbose) {
        waiting.removeIf(w -> w.process() == p);
        Status prev = status[p] == Status.WAITING ? Status.IDLE : status[p];
        active = p;
        reqVec = req.clone();
        reqProc = p;
        work = null;
        finishFlags = null;
        curChecks = new ArrayList<>();
        status[p] = Status.REQUESTING;

        emit(Kind.REQUEST, pn(p) + " нөөц хүсэж байна",
                pn(p) + " дараах нөөцийг хүсч байна: " + res(req) + ".\nСистем шууд өгөхгүй, эхлээд шалгана.",
                Code.REQUEST, -1, null);

        int[] need = st.getNeed()[p];
        int[] avail = st.getAvailable();
        boolean okNeed = leq(req, need);
        boolean okAvail = leq(req, avail);

        // 1-р шалгалт
        cChecks++;
        curChecks.add(new Check("Хүсэлт " + v(req) + " ≤ Need " + v(need), okNeed));
        if (verbose || !okNeed) {
            emit(Kind.CHECK, "1-р шалгалт: хэт их хүсэж байна уу?",
                    "Need = Max − Allocation = " + v(need) + ". Энэ нь " + pn(p)
                            + "-ийн дахин авах ёстой дээд хэмжээ. Процесс өөрийн зарласан хэрэгцээнээс илүү нөөц хүсч болохгүй.",
                    Code.REQUEST, 1, copy(curChecks));
        }
        if (!okNeed) {
            status[p] = prev;
            cDenied++;
            emit(Kind.ERROR, "Алдаа: хэт их хүсэлт",
                    pn(p) + " өөрийн хэрэгцээнээс илүү нөөц хүссэн тул хүсэлтийг шууд татгалзлаа.",
                    Code.REQUEST, 1, copy(curChecks));
            reqVec = null;
            return Res.ERROR;
        }

        // 2-р шалгалт
        cChecks++;
        curChecks.add(new Check("Хүсэлт " + v(req) + " ≤ Available " + v(avail), okAvail));
        if (verbose || !okAvail) {
            emit(Kind.CHECK, "2-р шалгалт: систем дээр нөөц байна уу?",
                    "Available = " + v(avail) + " нь системд яг одоо чөлөөтэй байгаа нөөц.",
                    Code.REQUEST, 2, copy(curChecks));
        } else {
            emit(Kind.CHECK, "Хоёр шалгалт тэнцлээ",
                    "Хүсэлт нь Need-ээс хэтрээгүй, системд нөөц ч хүрэлцэж байна.",
                    Code.REQUEST, 2, copy(curChecks));
        }
        if (!okAvail) {
            status[p] = Status.WAITING;
            addWaiting(p, req);
            cWaits++;
            emit(Kind.WAIT, pn(p) + " хүлээнэ",
                    "Системд яг одоо хүрэлцэх нөөц алга. " + pn(p)
                            + " түр хүлээнэ. Өөр процесс ажлаа дуусгаад нөөцөө буцаахад хүсэлтийг дахин шалгана.",
                    Code.REQUEST, 2, copy(curChecks));
            return Res.WAIT;
        }

        // Banker унтраалттай
        if (!sc.bankerOn()) {
            st.allocate(p, req);
            return grant(p, req, "Banker's Algorithm унтраалттай тул аюулгүй эсэхийг шалгалгүй шууд өглөө.");
        }

        // 3-р алхам: түр хуваарилах
        SystemState backup = st.copy();
        Sys prevSys = sys;
        List<Integer> prevSeq = seq;
        st.allocate(p, req);
        if (verbose) {
            emit(Kind.CHECK, "3-р алхам: нөөцийг түр өгч үзье",
                    "Нөөцийг өгчихлөө гэж төсөөлье (зөвхөн туршилт). Available "
                            + v(avail) + " → " + v(st.getAvailable()) + ", " + pn(p)
                            + "-ийн Allocation нэмэгдэж, Need буурлаа. Одоо ийм төлөв аюулгүй юу гэдгийг шалгана.",
                    Code.REQUEST, 3, copy(curChecks));
        }

        boolean safe;
        if (verbose) {
            safe = runSafety(false);
        } else {
            List<Integer> sq = new ArrayList<>();
            safe = BankerAlgorithm.checkSafety(st, sq);
            seq = sq;
            sys = safe ? Sys.SAFE : Sys.UNSAFE;
            cChecks++;
        }
        curChecks.add(new Check("Түр өгсний дараа систем SAFE хэвээр үү?", safe));

        if (safe) {
            return grant(p, req, "Түр өгсөн төлөв аюулгүй (Safe sequence: " + seqText(seq) + ") тул өглөө.");
        }

        // Аюултай → буцаана
        st.copyFrom(backup);
        sys = prevSys;
        seq = prevSeq;
        status[p] = Status.WAITING;
        addWaiting(p, req);
        cDenied++;
        emit(Kind.DENY, "✗ Хуваарилахгүй: аюултай",
                "Нөөц байгаа ч " + pn(p) + "-д өгвөл ямар ч процесс дуусаж чадахгүй болох эрсдэлтэй (UNSAFE). "
                        + "Түгжрэлээс сэргийлэхийн тулд түр өгсөн нөөцийг буцаалаа. " + pn(p)
                        + " хүлээнэ. Яг энэ нь Banker's Algorithm-ийн гол санаа.",
                Code.REQUEST, 6, copy(curChecks));
        return Res.DENIED;
    }

    private Res grant(int p, int[] req, String note) {
        waiting.removeIf(w -> w.process() == p);
        cGrants++;
        status[p] = Status.IDLE;
        emit(Kind.GRANT, "✓ Хүсэлт зөвшөөрөгдлөө",
                pn(p) + "-д " + res(req) + " нөөц өглөө. " + note + " Available одоо " + v(st.getAvailable()) + ".",
                Code.REQUEST, 5, copy(curChecks));
        if (isZero(st.getNeed()[p])) finishProcess(p);
        return Res.GRANTED;
    }

    private void finishProcess(int p) {
        int[] before = st.getAvailable();
        int[] held = st.getAllocation()[p];
        st.finish(p);
        status[p] = Status.FINISHED;
        cDone++;
        active = p;
        reqVec = null;
        work = null;
        finishFlags = null;
        emit(Kind.FINISH, "✔ " + pn(p) + " ажлаа дуусгалаа",
                pn(p) + " хэрэгцээгээ бүрэн авсан тул ажлаа гүйцэтгэж дууслаа. Эзэмшиж байсан "
                        + v(held) + " нөөцөө системд буцаалаа. Available: " + v(before) + " → " + v(st.getAvailable()) + ".",
                Code.NONE, -1, null);
        retryWaiting();
    }

    private void retryWaiting() {
        boolean progress = true;
        while (progress) {
            progress = false;
            for (Scenario.Req w : new ArrayList<>(waiting)) {
                if (st.isFinished(w.process())) {
                    waiting.remove(w);
                    continue;
                }
                if (!leq(w.amount(), st.getAvailable())) continue;
                waiting.remove(w);
                emit(Kind.INFO, "Хүлээж байсан хүсэлтийг дахин шалгая",
                        "Нөөц чөлөөлөгдсөн тул хүлээж байсан " + pn(w.process()) + "-ийн хүсэлт " + v(w.amount())
                                + "-ийг дахин оролдъё.", Code.NONE, -1, null);
                if (processRequest(w.process(), w.amount(), true) == Res.GRANTED) {
                    progress = true;
                    break;
                }
            }
        }
    }

    // ---------------------------------------------------------------- safety

    /** Safety (detect=false) эсвэл Deadlock Detection (detect=true) алгоритмыг алхам алхмаар тайлбарлана. */
    private boolean runSafety(boolean detect) {
        Kind kind = detect ? Kind.DETECT : Kind.SAFETY;
        Code code = detect ? Code.DETECT : Code.SAFETY;
        String need = detect ? "Request" : "Need";
        Status[] saved = status.clone();
        work = st.getAvailable();
        finishFlags = st.getFinished();
        seq = new ArrayList<>();

        emit(kind, detect ? "Deadlock Detection эхэллээ" : "Safety Algorithm эхэллээ",
                "Work = Available = " + v(work) + ". «Work» нь одоо боломжтой нөөцийн түр тооцоо. "
                        + "Ямар нэг процесс өөрийн үлдсэн хэрэгцээгээ (" + need + ") Work-оос авч дуусгаж чадах уу гэж хайна. "
                        + "Чадвал тэр процесс дуусаад нөөцөө буцаах тул Work өснө.",
                code, 1, null);

        int[][] needM = st.getNeed();
        int[][] alloc = st.getAllocation();
        int remaining = 0;
        for (boolean f : finishFlags) if (!f) remaining++;
        int total0 = remaining;

        while (true) {
            int found = -1;
            List<Check> cs = new ArrayList<>();
            for (int p = 0; p < n; p++) {
                if (finishFlags[p]) continue;
                cChecks++;
                boolean ok = leq(needM[p], work);
                cs.add(new Check(pn(p) + ": " + need + " " + v(needM[p]) + " ≤ Work " + v(work), ok));
                if (ok) {
                    found = p;
                    break;
                }
            }
            if (found < 0) {
                if (!cs.isEmpty()) {
                    emit(kind, "Дахин дуусаж чадах процесс олдсонгүй",
                            "Үлдсэн процессуудын хэрэгцээ Work-оос их байна. Хэн ч цааш явж чадахгүй.",
                            code, 4, cs);
                }
                break;
            }
            int[] before = work.clone();
            for (int j = 0; j < m; j++) work[j] += alloc[found][j];
            finishFlags[found] = true;
            seq.add(found);
            remaining--;
            status[found] = Status.CHECKING;
            active = found;
            emit(kind, pn(found) + " дуусаж чадна (" + seq.size() + "/" + total0 + ")",
                    pn(found) + "-ийн хэрэгцээ Work-д багтаж байна. Тэр ажлаа дуусгаад эзэмшиж байсан "
                            + v(alloc[found]) + " нөөцөө буцаана. Work: " + v(before) + " → " + v(work) + ".",
                    code, 3, cs);
            status[found] = saved[found];
        }

        boolean ok = remaining == 0;
        lastFlags = finishFlags.clone();
        if (!detect) {
            sys = ok ? Sys.SAFE : Sys.UNSAFE;
            if (ok) {
                emit(kind, "✓ Систем SAFE", "Бүх процесс ээлж дараалан ажлаа дуусгаж чадна. Safe sequence: "
                        + seqText(seq) + ". Иймд түгжрэл гарах аюул байхгүй.", code, 5, null);
            } else {
                emit(kind, "✗ Систем UNSAFE", "Safe sequence олдсонгүй. Дараах процессууд дуусаж чадахгүй: "
                        + names(finishFlags, false) + ". Энэ нь заавал түгжрэл болно гэсэн үг биш, гэхдээ түгжрэл болох эрсдэлтэй.",
                        code, 5, null);
            }
            active = -1;
        }
        work = null;
        finishFlags = null;
        System.arraycopy(saved, 0, status, 0, n);
        return ok;
    }

    private void finishDeadlock() {
        List<Integer> dead = new ArrayList<>();
        for (int p = 0; p < n; p++) {
            if (st.isFinished(p)) continue;
            if (lastFlags[p]) {
                st.finish(p);
                status[p] = Status.FINISHED;
                cDone++;
            } else {
                dead.add(p);
                status[p] = Status.DEADLOCKED;
            }
        }
        sys = Sys.DEADLOCK;
        active = -1;
        reqVec = null;
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < dead.size(); i++) names.append(i > 0 ? ", " : "").append(pn(dead.get(i)));
        int[][] needNow = st.getNeed();
        StringBuilder why = new StringBuilder();
        for (int d : dead) why.append(pn(d)).append(" дахин ").append(v(needNow[d])).append(" хэрэгтэй; ");
        emit(Kind.DEADLOCK, "ТҮГЖРЭЛ (DEADLOCK) гарлаа!",
                "Системд чөлөөтэй нөөц " + v(st.getAvailable()) + " л үлдсэн. " + why
                        + "тиймээс хэн ч цааш явж чадахгүй. "
                        + names + " нь бие биеийнхээ барьж буй нөөцийг хүлээсээр хэзээ ч ажлаа дуусгаж чадахгүй. "
                        + "Яг энэ нь Banker's Algorithm-ийн сэргийлэх гэсэн зүйл. Ердийн хуваарилалт (Banker-гүй) энэ төлөвт орох боломжтой.",
                Code.DETECT, 5, null);
    }

    // ---------------------------------------------------------------- helpers

    private void addWaiting(int p, int[] req) {
        waiting.removeIf(w -> w.process() == p);
        waiting.add(new Scenario.Req(p, req.clone()));
    }

    private boolean allFinished() {
        for (int p = 0; p < n; p++) if (!st.isFinished(p)) return false;
        return true;
    }

    private static boolean isZero(int[] a) {
        for (int x : a) if (x != 0) return false;
        return true;
    }

    private static String pn(int p) {
        return "P" + p;
    }

    private static String v(int[] a) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < a.length; i++) sb.append(i > 0 ? "," : "").append(a[i]);
        return sb.append(")").toString();
    }

    private static String res(int[] a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; i++) sb.append(i > 0 ? ", " : "").append("R").append(i).append(": ").append(a[i]);
        return sb.toString();
    }

    private static String seqText(List<Integer> s) {
        StringBuilder sb = new StringBuilder("⟨");
        for (int i = 0; i < s.size(); i++) sb.append(i > 0 ? ", " : "").append("P").append(s.get(i));
        return sb.append("⟩").toString();
    }

    private static String names(boolean[] flags, boolean value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] == value) sb.append(sb.length() > 0 ? ", " : "").append("P").append(i);
        }
        return sb.toString();
    }

    private static List<Check> copy(List<Check> l) {
        return new ArrayList<>(l);
    }

    private void emit(Kind kind, String title, String text, Code code, int line, List<Check> checks) {
        Step s = new Step();
        s.kind = kind;
        s.title = title;
        s.text = text;
        s.code = code;
        s.codeLine = line;
        s.checks = checks == null ? List.of() : checks;
        s.total = total;
        s.available = st.getAvailable();
        s.allocation = st.getAllocation();
        s.max = st.getMax();
        s.need = st.getNeed();
        s.finished = st.getFinished();
        s.status = status.clone();
        s.active = active;
        s.request = reqVec == null ? null : reqVec.clone();
        s.requester = reqVec == null ? -1 : reqProc;
        s.work = work == null ? null : work.clone();
        s.finishFlags = finishFlags == null ? null : finishFlags.clone();
        s.safeSeq = new ArrayList<>(seq);
        s.waiting = new ArrayList<>(waiting);
        s.sys = sys;
        s.bankerOn = sc.bankerOn();
        s.cChecks = cChecks;
        s.cGrants = cGrants;
        s.cWaits = cWaits;
        s.cDenied = cDenied;
        s.cDone = cDone;
        steps.add(s);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: com/os/Step/Check#