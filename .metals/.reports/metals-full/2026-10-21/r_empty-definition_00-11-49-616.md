error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java:java/util/Arrays#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java
empty definition using pc, found symbol in pc: java/util/Arrays#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 248
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MainFrame.java
text:
```scala
package main.java.com.os;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.@@Arrays;
import java.util.List;

public class MainFrame extends JFrame {
    private static final Color ACTIVE_ROW = new Color(255, 243, 176);

    private final SystemState initialState = createInitialState();
    private final SystemState state = initialState.copy();
    private final ResourceGraphPanel graphPanel = new ResourceGraphPanel(state);
    private final JTextArea logArea = new JTextArea(6, 80);

    private DefaultTableModel allocModel;
    private DefaultTableModel maxModel;
    private DefaultTableModel needModel;
    private DefaultTableModel availModel;

    private int activeProcess = -1;

    public MainFrame() {
        setTitle("II-4-13: Banker's Algorithm with Resource Graph");
        setSize(1250, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refresh();
    }

    // Анхны өгөгдөл (Operating System Concepts сурах бичгийн жишээ) - ганц газарт
    private static SystemState createInitialState() {
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
        return new SystemState(5, 3, available, max, allocation);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Дээд талд удирдлагын панел
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton btnCheckSafety = new JButton("Check Safety");
        JButton btnRequest = new JButton("Send Request");
        JButton btnReset = new JButton("Reset State");
        controlPanel.add(btnCheckSafety);
        controlPanel.add(btnRequest);
        controlPanel.add(btnReset);
        add(controlPanel, BorderLayout.NORTH);

        // Голд: зүүн талд граф, баруун талд хүснэгтүүд
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel, createTablesPanel());
        split.setResizeWeight(0.6);
        split.setDividerLocation(700);
        add(split, BorderLayout.CENTER);

        // Доор лог панел
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
            refresh();
        });

        btnRequest.addActionListener(e -> onSendRequest());

        btnReset.addActionListener(e -> {
            state.copyFrom(initialState);
            activeProcess = -1;
            logArea.setText(">> Системийн төлөвийг анхны утганд шинэчлэлээ.\n");
            refresh();
        });
    }

    private void onSendRequest() {
        try {
            String pStr = JOptionPane.showInputDialog(this,
                    "Процессын ID оруулах (0 - " + (state.getNumProcesses() - 1) + "):");
            if (pStr == null) return;
            int p = Integer.parseInt(pStr.trim());

            String reqStr = JOptionPane.showInputDialog(this,
                    "Нөөцийн хүсэлт оруулах (" + state.getNumResources() + " утга, жишээ нь: 1,0,2):");
            if (reqStr == null) return;
            int[] req = Arrays.stream(reqStr.split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim()))
                    .toArray();

            BankerAlgorithm.RequestResult result = BankerAlgorithm.requestResources(state, p, req);
            logArea.append(">> " + result.message() + "\n");
            if (p >= 0 && p < state.getNumProcesses()) {
                activeProcess = p;
            }
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Зөвхөн бүхэл тоо оруулна уу.",
                    "Алдаа", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Хүснэгтүүд ----------

    private JPanel createTablesPanel() {
        int n = state.getNumProcesses();
        int m = state.getNumResources();

        String[] resCols = new String[m];
        for (int j = 0; j < m; j++) resCols[j] = "R" + j;

        String[] procCols = new String[m + 1];
        procCols[0] = "Process";
        System.arraycopy(resCols, 0, procCols, 1, m);

        allocModel = createModel(procCols);
        maxModel = createModel(procCols);
        needModel = createModel(procCols);
        availModel = createModel(resCols);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridy = 0; gbc.weighty = n;
        panel.add(createTableScroll("Allocation", allocModel, true), gbc);
        gbc.gridy = 1; gbc.weighty = n;
        panel.add(createTableScroll("Max", maxModel, true), gbc);
        gbc.gridy = 2; gbc.weighty = n;
        panel.add(createTableScroll("Need (Max - Allocation)", needModel, true), gbc);
        gbc.gridy = 3; gbc.weighty = 2;
        panel.add(createTableScroll("Available", availModel, false), gbc);

        return panel;
    }

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JScrollPane createTableScroll(String title, DefaultTableModel model, boolean highlightActiveRow) {
        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                            boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!selected) {
                    c.setBackground(highlightActiveRow && row == activeProcess ? ACTIVE_ROW : Color.WHITE);
                }
                return c;
            }
        };
        for (int c = 0; c < table.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 12)));
        return scroll;
    }

    private void fillMatrix(DefaultTableModel model, int[][] data) {
        model.setRowCount(0);
        for (int i = 0; i < data.length; i++) {
            Object[] row = new Object[data[i].length + 1];
            row[0] = "P" + i;
            for (int j = 0; j < data[i].length; j++) row[j + 1] = data[i][j];
            model.addRow(row);
        }
    }

    /** Хүснэгт, граф болон идэвхтэй процессыг шинэчилнэ. */
    private void refresh() {
        fillMatrix(allocModel, state.getAllocation());
        fillMatrix(maxModel, state.getMax());
        fillMatrix(needModel, state.getNeed());

        availModel.setRowCount(0);
        int[] avail = state.getAvailable();
        Object[] row = new Object[avail.length];
        for (int j = 0; j < avail.length; j++) row[j] = avail[j];
        availModel.addRow(row);

        graphPanel.setActiveProcess(activeProcess); // repaint дотроо дуудагдана
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Arrays#