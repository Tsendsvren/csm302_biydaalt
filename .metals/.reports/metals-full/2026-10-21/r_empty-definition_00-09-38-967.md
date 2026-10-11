error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java:java/util/List#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java
empty definition using pc, found symbol in pc: java/util/List#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 113
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java
text:
```scala
package main.java.com.os;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.@@List;

public class MainFrame extends JFrame {
    private SystemState state;
    private ResourceGraphPanel graphPanel;
    private JTextArea logArea;

    public MainFrame() {
        setTitle("II-4-13: Banker's Algorithm with Resource Graph");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Жишээ өгөгдөл эхлүүлэх (Operating System Concepts сурах бичгийн бэлэн жишээ)
        int[] available = {3, 3, 2};
        int[][] max = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 2},
            {2, 2, 2},
            {4, 3, 3}
        };
        int[][] allocation = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 2},
            {2, 1, 1},
            {0, 0, 2}
        };

        state = new SystemState(5, 3, available, max, allocation);
        graphPanel = new ResourceGraphPanel(state);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Баруун талд Graph Panel
        add(graphPanel, BorderLayout.CENTER);

        // Доод талд Удирдлагын панел
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton btnCheckSafety = new JButton("Check Safety");
        JButton btnRequest = new JButton("Send Request");
        JButton btnReset = new JButton("Reset State");

        controlPanel.add(btnCheckSafety);
        controlPanel.add(btnRequest);
        controlPanel.add(btnReset);

        add(controlPanel, BorderLayout.NORTH);

        // Доор Лог бичих панел
        logArea = new JTextArea(6, 80);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        // Эвентүүд
        btnCheckSafety.addActionListener(e -> {
            List<Integer> seq = new ArrayList<>();
            boolean safe = BankerAlgorithm.checkSafety(state, seq);
            if (safe) {
                logArea.append(">> Систем SAFE төлөвт байна. Safe Sequence: " + seq + "\n");
            } else {
                logArea.append(">> АНХААР: Систем UNSAFE төлөвт байна!\n");
            }
            graphPanel.repaint();
        });

        btnRequest.addActionListener(e -> {
            String pStr = JOptionPane.showInputDialog(this, "Процессын ID оруулах (0 - 4):");
            if (pStr == null) return;
            int p = Integer.parseInt(pStr);

            String reqStr = JOptionPane.showInputDialog(this, "Нөөцийн хүсэлт оруулах (жишээ нь: 1,0,2):");
            if (reqStr == null) return;
            String[] parts = reqStr.split(",");
            int[] req = new int[parts.length];
            for (int i = 0; i < parts.length; i++) req[i] = Integer.parseInt(parts[i].trim());

            String res = BankerAlgorithm.requestResources(state, p, req);
            logArea.append(">> " + res + "\n");
            graphPanel.repaint();
        });

        btnReset.addActionListener(e -> {
            int[] available = {3, 3, 2};
            int[][] max = {{7, 5, 3}, {3, 2, 2}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}};
            int[][] allocation = {{0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}};
            state.setAvailable(available);
            state.setMax(max);
            state.setAllocation(allocation);
            logArea.setText(">> Системийн төлөвийг анхны утганд шинэчлэлээ.\n");
            graphPanel.repaint();
        });
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/List#