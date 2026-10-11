error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulatorPanel.java:java/util/List#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulatorPanel.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 75
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/SimulatorPanel.java
text:
```scala
package com.os;

import javax.swing.*;
import java.awt.*;
import java.util.@@List;


public class SimulatorPanel extends JPanel {
    private final List<Scenario> scenarios = ScenarioLibrary.all();

    private final JComboBox<Scenario> scenarioBox = new JComboBox<>(scenarios.toArray(new Scenario[0]));
    private final JTextArea storyArea = new JTextArea(2, 80);
    private final ResourceGraphPanel graph = new ResourceGraphPanel();
    private final MatrixPanel matrix = new MatrixPanel();
    private final CodeViewerPanel code = new CodeViewerPanel();
    private final ExplainPanel explain = new ExplainPanel();
    private final JLabel statsLabel = new JLabel();

    private final JButton btnFirst = new JButton("⏮");
    private final JButton btnPrev = new JButton("◀ Өмнөх");
    private final JButton btnPlay = new JButton("▶ Тоглуулах");
    private final JButton btnNext = new JButton("Дараах ▶");
    private final JButton btnLast = new JButton("⏭");
    private final JSlider seek = new JSlider(0, 0, 0);
    private final JSlider speed = new JSlider(1, 10, 4);
    private final JLabel stepLabel = new JLabel();

    private final Timer timer;
    private List<Step> steps = List.of();
    private int index = 0;
    private boolean adjusting = false;

    public SimulatorPanel() {
        setLayout(new BorderLayout());

        timer = new Timer(2200, e -> {
            if (index < steps.size() - 1) {
                show(index + 1);
            } else {
                stop();
            }
        });

        add(buildTop(), BorderLayout.NORTH);

        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(matrix), code);
        right.setResizeWeight(0.75);
        right.setDividerLocation(385);
        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graph, right);
        center.setResizeWeight(0.5);
        center.setDividerLocation(660);
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        explain.setPreferredSize(new Dimension(800, 170));
        south.add(explain, BorderLayout.CENTER);
        statsLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statsLabel.setOpaque(true);
        statsLabel.setBackground(new Color(236, 240, 241));
        south.add(statsLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        wireEvents();
        loadScenario(scenarios.get(0));
    }

    private JPanel buildTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel row1 = new JPanel(new BorderLayout(8, 0));
        JLabel title = new JLabel("Banker's Algorithm симуляци");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        row1.add(title, BorderLayout.WEST);
        scenarioBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        row1.add(scenarioBox, BorderLayout.CENTER);
        JButton help = new JButton("? Ойлголтууд");
        help.addActionListener(e -> showHelp());
        row1.add(help, BorderLayout.EAST);
        top.add(row1);
        top.add(Box.createVerticalStrut(4));

        storyArea.setEditable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);
        storyArea.setBackground(new Color(255, 250, 224));
        storyArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        storyArea.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        top.add(storyArea);
        top.add(Box.createVerticalStrut(4));

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row3.add(btnFirst);
        row3.add(btnPrev);
        row3.add(btnPlay);
        row3.add(btnNext);
        row3.add(btnLast);
        seek.setPreferredSize(new Dimension(300, 24));
        row3.add(seek);
        row3.add(stepLabel);
        row3.add(Box.createHorizontalStrut(12));
        row3.add(new JLabel("Хурд:"));
        speed.setPreferredSize(new Dimension(110, 24));
        row3.add(speed);
        top.add(row3);
        return top;
    }

    private void wireEvents() {
        scenarioBox.addActionListener(e -> loadScenario((Scenario) scenarioBox.getSelectedItem()));
        btnFirst.addActionListener(e -> { stop(); show(0); });
        btnPrev.addActionListener(e -> { stop(); show(index - 1); });
        btnNext.addActionListener(e -> { stop(); show(index + 1); });
        btnLast.addActionListener(e -> { stop(); show(steps.size() - 1); });
        btnPlay.addActionListener(e -> {
            if (timer.isRunning()) {
                stop();
            } else {
                if (index >= steps.size() - 1) show(0);
                start();
            }
        });
        seek.addChangeListener(e -> {
            if (!adjusting) {
                stop();
                show(seek.getValue());
            }
        });
        speed.addChangeListener(e -> timer.setDelay(3400 - speed.getValue() * 300));
        timer.setDelay(3400 - speed.getValue() * 300);
    }

    private void start() {
        timer.start();
        btnPlay.setText("⏸ Зогсоох");
    }

    private void stop() {
        timer.stop();
        btnPlay.setText("▶ Тоглуулах");
    }

    private void loadScenario(Scenario sc) {
        stop();
        steps = SimulationEngine.run(sc);
        storyArea.setText("Энэ жишээ юуг харуулах вэ?  " + sc.story());
        adjusting = true;
        seek.setMaximum(steps.size() - 1);
        adjusting = false;
        show(0);
    }

    private void show(int i) {
        if (steps.isEmpty()) return;
        index = Math.max(0, Math.min(i, steps.size() - 1));
        Step s = steps.get(index);

        graph.setStep(s);
        matrix.setStep(s);
        code.setStep(s);
        explain.setStep(s, index, steps.size());

        adjusting = true;
        seek.setValue(index);
        adjusting = false;
        stepLabel.setText((index + 1) + " / " + steps.size());
        btnPrev.setEnabled(index > 0);
        btnFirst.setEnabled(index > 0);
        btnNext.setEnabled(index < steps.size() - 1);
        btnLast.setEnabled(index < steps.size() - 1);

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("Шалгалт: <b>").append(s.cChecks).append("</b> &nbsp;|&nbsp; ")
          .append("Зөвшөөрсөн: <b>").append(s.cGrants).append("</b> &nbsp;|&nbsp; ")
          .append("Хүлээлгэсэн: <b>").append(s.cWaits).append("</b> &nbsp;|&nbsp; ")
          .append("Татгалзсан: <b>").append(s.cDenied).append("</b> &nbsp;|&nbsp; ")
          .append("Дууссан: <b>").append(s.cDone).append(" / ").append(s.allocation.length).append("</b>");
        if (!s.safeSeq.isEmpty()) {
            sb.append(" &nbsp;|&nbsp; Safe sequence: <b>⟨");
            for (int k = 0; k < s.safeSeq.size(); k++) sb.append(k > 0 ? ", " : "").append("P").append(s.safeSeq.get(k));
            sb.append("⟩</b>");
        }
        sb.append(" &nbsp;|&nbsp; <span style='color:#7f8c8d'>Safety Algorithm: O(m·n²)</span></html>");
        statsLabel.setText(sb.toString());
    }

    private void showHelp() {
        String html = "<html><body style='width:520px; font-family:SansSerif;'>"
                + "<h3>Банкирын алгоритм гэж юу вэ?</h3>"
                + "<p>Банкир олон үйлчлүүлэгчид зээл олгодог гэж төсөөл. Үйлчлүүлэгч бүр «надад нийтдээ хэд хэрэгтэй» гэж "
                + "урьдчилан зарласан. Банкир мөнгөө өгөхөөсөө өмнө «өгсний дараа бүх үйлчлүүлэгч эцэст нь хэрэгтэй "
                + "мөнгөө авч, ажлаа дуусгаж чадах уу?» гэж бодно. Чадахгүй бол өгөхгүй. Үүгээр түгжрэлээс сэргийлнэ.</p>"
                + "<ul>"
                + "<li><b>Процесс (P)</b> - нөөц хэрэгтэй ажил, програм.</li>"
                + "<li><b>Нөөц (R)</b> - принтер, санах ой, файл гэх мэт. Төрөл бүр хэд хэдэн нэгжтэй.</li>"
                + "<li><b>Max</b> - процесс нийтдээ хамгийн ихдээ хэд хэрэгтэй гэж зарласан хэмжээ.</li>"
                + "<li><b>Allocation</b> - процесс яг одоо барьж буй нөөц.</li>"
                + "<li><b>Need</b> = Max − Allocation: процесс дахин авах ёстой нөөц.</li>"
                + "<li><b>Available</b> - системд яг одоо чөлөөтэй байгаа нөөц.</li>"
                + "<li><b>SAFE</b> - бүх процесс ээлж дараалан ажлаа дуусгаж чадах дараалал (Safe sequence) байна.</li>"
                + "<li><b>UNSAFE</b> - ийм дараалал олдохгүй. Түгжрэл болох эрсдэлтэй.</li>"
                + "<li><b>DEADLOCK</b> - процессууд бие биеийнхээ барьсан нөөцийг хүлээж, хэзээ ч дуусахгүй.</li>"
                + "</ul>"
                + "<p><b>Дүрэм:</b> хүсэлт Need-ээс хэтрэхгүй, Available-д багтаж, өгсний дараа систем SAFE хэвээр үлдэх "
                + "тохиолдолд л нөөц олгоно. Үгүй бол процесс хүлээнэ.</p>"
                + "<p style='color:#666'>Эх сурвалж: Silberschatz, Galvin, Gagne, Operating System Concepts, Chapter 8.</p>"
                + "</body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(html), "Ойлголтууд", JOptionPane.INFORMATION_MESSAGE);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#