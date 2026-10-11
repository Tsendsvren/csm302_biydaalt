error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ResourceGraphPanel.java:java/awt/geom/AffineTransform#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ResourceGraphPanel.java
empty definition using pc, found symbol in pc: java/awt/geom/AffineTransform#
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
import java.awt.geom.@@AffineTransform;

public class ResourceGraphPanel extends JPanel {
    private SystemState state;
    private int activeProcess = -1;

    public ResourceGraphPanel(SystemState state) {
        this.state = state;
        this.setBackground(Color.WHITE);
    }

    public void setActiveProcess(int activeProcess) {
        this.activeProcess = activeProcess;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = state.getNumProcesses();
        int m = state.getNumResources();

        int width = getWidth();
        int height = getHeight();

        Point[] processPos = new Point[n];
        Point[] resourcePos = new Point[m];

        // Процессуудыг зүүн талд жагсааж зурах
        for (int i = 0; i < n; i++) {
            int x = width / 4;
            int y = (i + 1) * height / (n + 1);
            processPos[i] = new Point(x, y);
        }

        // Нөөцүүдийг баруун талд жагсааж зурах
        for (int j = 0; j < m; j++) {
            int x = 3 * width / 4;
            int y = (j + 1) * height / (m + 1);
            resourcePos[j] = new Point(x, y);
        }

        // 1. Assignment Edges (Resource -> Process)
        g2.setColor(new Color(41, 128, 185)); // Цэнхэр сум
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (state.getAllocation()[i][j] > 0) {
                    drawArrow(g2, resourcePos[j].x, resourcePos[j].y, processPos[i].x, processPos[i].y, 
                            "Alloc: " + state.getAllocation()[i][j]);
                }
            }
        }

        // 2. Request Edges (Process -> Resource)
        g2.setColor(new Color(230, 126, 34)); // Улбар шар сум
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (state.getNeed()[i][j] > 0) {
                    drawArrow(g2, processPos[i].x, processPos[i].y, resourcePos[j].x, resourcePos[j].y, 
                            "Need: " + state.getNeed()[i][j]);
                }
            }
        }

        // 3. Process Nodes (Circles)
        for (int i = 0; i < n; i++) {
            Point p = processPos[i];
            if (i == activeProcess) {
                g2.setColor(new Color(241, 196, 15)); // Идэвхтэй процесс - Шар тодруулга
                g2.fillOval(p.x - 25, p.y - 25, 50, 50);
            }
            g2.setColor(new Color(52, 152, 219));
            g2.fillOval(p.x - 20, p.y - 20, 40, 40);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString("P" + i, p.x - 9, p.y + 5);
        }

        // 4. Resource Nodes (Rectangles)
        for (int j = 0; j < m; j++) {
            Point r = resourcePos[j];
            g2.setColor(new Color(39, 174, 96));
            g2.fillRect(r.x - 25, r.y - 25, 50, 50);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("R" + j, r.x - 10, r.y - 5);
            g2.drawString("Avail:" + state.getAvailable()[j], r.x - 20, r.y + 15);
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2, String label) {
        int ARR_SIZE = 8;
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g2.transform(at);

        g2.drawLine(25, 0, len - 25, 0);
        g2.fillPolygon(new int[]{len - 25, len - 25 - ARR_SIZE, len - 25 - ARR_SIZE},
                    new int[]{0, -ARR_SIZE, ARR_SIZE}, 3);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString(label, len / 2 - 15, -5);

        try {
            g2.transform(at.createInverse());
        } catch (Exception ignored) {}
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/awt/geom/AffineTransform#