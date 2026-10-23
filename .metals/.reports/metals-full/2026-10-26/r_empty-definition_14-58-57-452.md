error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MatrixPanel.java:
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MatrixPanel.java
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1714
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/MatrixPanel.java
text:
```scala
package com.os;

import com.os.Step.Status;

import javax.swing.*;
import java.awt.*;

public class MatrixPanel extends JPanel {
    private static final int RH = 22, NAME_W = 44, STATE_W = 128, GAP = 12, PAD = 10;

    private Step step;
    private int cw = 34;

    public MatrixPanel() {
        setBackground(Color.WHITE);
    }

    public void setStep(Step step) {
        this.step = step;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (step == null) return new Dimension(400, 200);
        int n = step.allocation.length;
        int m = step.available.length;
        cw = Math.max(34, 86 / m);
        int w = PAD * 2 + NAME_W + STATE_W + 3 * (m * cw + GAP);
        int h = PAD * 2 + 2 * RH + n * RH + 10 + 4 * RH + 6;
        return new Dimension(w, h);
    }

    private static Color rowColor(Status s) {
        switch (s) {
            case REQUESTING: return new Color(255, 232, 204);
            case CHECKING:   return new Color(255, 245, 190);
            case WAITING:    return new Color(236, 224, 245);
            case FINISHED:   return new Color(222, 245, 230);
            case DEADLOCKED: return new Color(250, 214, 210);
            default:         return Color.WHITE;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (step == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            paintAll(g2);
        } finally {
            g2.dispose();
        }
    }

    private void paintAll(Graphics2D@@ g2) {
        int n = step.allocation.length;
        int m = step.available.length;
        cw = Math.max(34, 86 / m);
        int gw = m * cw;
        int x0 = PAD, y0 = PAD;
        int xAlloc = x0 + NAME_W + STATE_W;
        int xMax = xAlloc + gw + GAP;
        int xNeed = xMax + gw + GAP;

        Font bold = new Font("SansSerif", Font.BOLD, 12);
        Font plain = new Font("SansSerif", Font.PLAIN, 12);

        g2.setFont(bold);
        groupHeader(g2, xAlloc, y0, gw, "Allocation", new Color(41, 128, 185));
        groupHeader(g2, xMax, y0, gw, "Max", new Color(142, 68, 173));
        groupHeader(g2, xNeed, y0, gw, "Need", new Color(211, 84, 0));

        // Баганы гарчиг
        int yh = y0 + RH;
        g2.setFont(bold);
        centered(g2, "Процесс", x0, yh, NAME_W, RH, Color.DARK_GRAY);
        centered(g2, "Төлөв", x0 + NAME_W, yh, STATE_W, RH, Color.DARK_GRAY);
        for (int g = 0; g < 3; g++) {
            int gx = g == 0 ? xAlloc : g == 1 ? xMax : xNeed;
            for (int j = 0; j < m; j++) centered(g2, "R" + j, gx + j * cw, yh, cw, RH, Color.DARK_GRAY);
        }

        // Мөрүүд
        int y = yh + RH;
        for (int i = 0; i < n; i++) {
            Status s = step.status[i];
            g2.setColor(rowColor(s));
            g2.fillRect(x0, y, xNeed + gw - x0, RH);
            if (i == step.active) {
                g2.setColor(new Color(241, 196, 15));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(x0, y, xNeed + gw - x0, RH);
            }
            boolean done = s == Status.FINISHED;
            g2.setFont(bold);
            centered(g2, "P" + i, x0, y, NAME_W, RH, done ? Color.GRAY : Color.BLACK);
            g2.setFont(plain);
            String txt = ResourceGraphPanel.statusText(s);
            if (done) txt = "✓ дууссан";
            if (step.finishFlags != null && step.finishFlags[i] && !done) txt = "✓ дуусаж чадна";
            centered(g2, txt, x0 + NAME_W, y, STATE_W, RH, ResourceGraphPanel.statusColor(s).darker());
            for (int j = 0; j < m; j++) {
                Color fg = done ? Color.LIGHT_GRAY : Color.BLACK;
                centered(g2, String.valueOf(step.allocation[i][j]), xAlloc + j * cw, y, cw, RH, fg);
                centered(g2, String.valueOf(step.max[i][j]), xMax + j * cw, y, cw, RH, fg);
                boolean hot = step.request != null && i == step.requester && step.request[j] > 0;
                centered(g2, String.valueOf(step.need[i][j]), xNeed + j * cw, y, cw, RH,
                        hot ? new Color(211, 84, 0) : fg);
            }
            // Хөнгөн шугам
            g2.setColor(new Color(225, 225, 225));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(x0, y + RH, xNeed + gw, y + RH);
            y += RH;
        }
        // Босоо тусгаарлагч
        g2.setColor(new Color(200, 200, 200));
        for (int xx : new int[]{xAlloc - 2, xMax - 2, xNeed - 2}) g2.drawLine(xx, yh, xx, y);

        // Векторууд
        y += 10;
        vectorRow(g2, x0, xAlloc, y, "Total (нийт нөөц)", step.total, new Color(245, 245, 245), Color.DARK_GRAY, gw);
        y += RH;
        vectorRow(g2, x0, xAlloc, y, "Available (бэлэн)", step.available, new Color(222, 245, 230), new Color(30, 110, 60), gw);
        y += RH;
        vectorRow(g2, x0, xAlloc, y, "Work (түр тооцоо)", step.work, new Color(255, 245, 190), new Color(130, 100, 0), gw);
        y += RH;
        if (step.request != null) {
            vectorRow(g2, x0, xAlloc, y, "Хүсэлт (Request)", step.request, new Color(255, 232, 204), new Color(211, 84, 0), gw);
        }
    }

    private void groupHeader(Graphics2D g2, int x, int y, int w, String text, Color c) {
        g2.setColor(c);
        g2.fillRect(x, y, w, RH);
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        String t = text;
        while (fm.stringWidth(t) > w - 4 && t.length() > 4) t = t.substring(0, t.length() - 2);
        g2.drawString(t, x + (w - fm.stringWidth(t)) / 2, y + (RH + fm.getAscent()) / 2 - 2);
    }

    private void vectorRow(Graphics2D g2, int x0, int xCells, int y, String label, int[] v,
                           Color bg, Color fg, int gw) {
        g2.setColor(bg);
        g2.fillRect(x0, y, xCells + gw - x0, RH);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(fg);
        g2.drawString(label, x0 + 4, y + RH - 7);
        for (int j = 0; j < step.available.length; j++) {
            String s = v == null ? "—" : String.valueOf(v[j]);
            centered(g2, s, xCells + j * cw, y, cw, RH, fg);
        }
    }

    private void centered(Graphics2D g2, String text, int x, int y, int w, int h, Color c) {
        g2.setColor(c);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (w - fm.stringWidth(text)) / 2, y + (h + fm.getAscent()) / 2 - 2);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 