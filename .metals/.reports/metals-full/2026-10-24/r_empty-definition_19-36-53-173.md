error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ResourceGraphPanel.java:java/awt/geom/Line2D#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ResourceGraphPanel.java
empty definition using pc, found symbol in pc: java/awt/geom/Line2D#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 89
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ResourceGraphPanel.java
text:
```scala
package main.java.com.os;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.@@Line2D;
import java.awt.geom.Path2D;

public class ResourceGraphPanel extends JPanel {
    private static final int NODE_R = 26;          // сумны эхлэл/төгсгөлийн зай
    private static final double EDGE_OFFSET = 8;   // Alloc ба Need сумыг зэрэгцүүлэх шилжилт
    private static final Color ALLOC_COLOR = new Color(41, 128, 185);
    private static final Color NEED_COLOR = new Color(230, 126, 34);
    private static final Stroke SOLID = new BasicStroke(2f);
    private static final Stroke DASHED = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f);

    private final SystemState state;
    private int activeProcess = -1;

    public ResourceGraphPanel(SystemState state) {
        this.state = state;
        setBackground(Color.WHITE);
    }

    public void setActiveProcess(int activeProcess) {
        this.activeProcess = activeProcess;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            draw(g2);
        } finally {
            g2.dispose();
        }
    }

    private void draw(Graphics2D g2) {
        int n = state.getNumProcesses();
        int m = state.getNumResources();
        int width = getWidth();
        int height = getHeight();

        int[][] allocation = state.getAllocation();
        int[][] need = state.getNeed();
        int[] available = state.getAvailable();

        Point[] processPos = new Point[n];
        Point[] resourcePos = new Point[m];

        // Процессуудыг зүүн талд жагсааж зурах
        for (int i = 0; i < n; i++) {
            processPos[i] = new Point(width / 4, (i + 1) * height / (n + 1));
        }
        // Нөөцүүдийг баруун талд жагсааж зурах
        for (int j = 0; j < m; j++) {
            resourcePos[j] = new Point(3 * width / 4, (j + 1) * height / (m + 1));
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // 1. Assignment Edges (Resource -> Process), тасралтгүй цэнхэр
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (allocation[i][j] > 0) {
                    drawEdge(g2, resourcePos[j], processPos[i], "Alloc: " + allocation[i][j],
                            false, ALLOC_COLOR);
                }
            }
        }

        // 2. Claim/Need Edges (Process -> Resource), тасархай улбар шар
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (need[i][j] > 0) {
                    drawEdge(g2, processPos[i], resourcePos[j], "Need: " + need[i][j],
                            true, NEED_COLOR);
                }
            }
        }

        // 3. Process Nodes (Circles)
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        for (int i = 0; i < n; i++) {
            Point p = processPos[i];
            if (i == activeProcess) {
                g2.setColor(new Color(241, 196, 15)); // Идэвхтэй процесс - шар тодруулга
                g2.fillOval(p.x - 25, p.y - 25, 50, 50);
            }
            g2.setColor(new Color(52, 152, 219));
            g2.fillOval(p.x - 20, p.y - 20, 40, 40);
            g2.setColor(Color.WHITE);
            drawCentered(g2, "P" + i, p.x, p.y + 5);
        }

        // 4. Resource Nodes (Rectangles)
        for (int j = 0; j < m; j++) {
            Point r = resourcePos[j];
            g2.setColor(new Color(39, 174, 96));
            g2.fillRect(r.x - 25, r.y - 25, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawCentered(g2, "R" + j, r.x, r.y - 4);
            drawCentered(g2, "Avail:" + available[j], r.x, r.y + 14);
        }

        drawLegend(g2, height);
    }

    /**
     * Сум зурна. Текстийг эргүүлэхгүй тул label үргэлж зөв чиглэлтэй харагдана.
     * Resource->Process ба Process->Resource сум ижил offset-тэй ч эсрэг талд
     * гарах тул давхцахгүй.
     */
    private void drawEdge(Graphics2D g2, Point from, Point to, String label,
                          boolean dashed, Color color) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double len = Math.hypot(dx, dy);
        if (len < 2 * NODE_R) return;

        double ux = dx / len, uy = dy / len;                 // чиглэлийн нэгж вектор
        double nx = -uy * EDGE_OFFSET, ny = ux * EDGE_OFFSET; // перпендикуляр шилжилт

        double sx = from.x + nx + ux * NODE_R;
        double sy = from.y + ny + uy * NODE_R;
        double ex = to.x + nx - ux * NODE_R;
        double ey = to.y + ny - uy * NODE_R;

        g2.setColor(color);
        g2.setStroke(dashed ? DASHED : SOLID);
        g2.draw(new Line2D.Double(sx, sy, ex, ey));

        // Сумны толгой
        g2.setStroke(SOLID);
        double a = 9;
        Path2D head = new Path2D.Double();
        head.moveTo(ex, ey);
        head.lineTo(ex - ux * a + (-uy) * a / 2, ey - uy * a + ux * a / 2);
        head.lineTo(ex - ux * a - (-uy) * a / 2, ey - uy * a - ux * a / 2);
        head.closePath();
        g2.fill(head);

        // Label: шугамын голд, шугамын гадна тал руу
        double mx = (sx + ex) / 2 + nx * 1.6;
        double my = (sy + ey) / 2 + ny * 1.6;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRect((int) mx - tw / 2 - 1, (int) my - fm.getAscent() + 2, tw + 2, fm.getHeight() - 2);
        g2.setColor(color.darker());
        g2.drawString(label, (int) mx - tw / 2, (int) my + 2);
    }

    private void drawCentered(Graphics2D g2, String text, int cx, int baseline) {
        int w = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, cx - w / 2, baseline);
    }

    private void drawLegend(Graphics2D g2, int height) {
        int x = 12, y = height - 40;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        g2.setStroke(SOLID);
        g2.setColor(ALLOC_COLOR);
        g2.drawLine(x, y, x + 28, y);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Assignment edge (Allocation)", x + 36, y + 4);

        g2.setStroke(DASHED);
        g2.setColor(NEED_COLOR);
        g2.drawLine(x, y + 18, x + 28, y + 18);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Claim edge (Need)", x + 36, y + 22);
        g2.setStroke(SOLID);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/awt/geom/Line2D#