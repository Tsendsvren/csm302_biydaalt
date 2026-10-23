package com.os;

import com.os.Step.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;


public class ResourceGraphPanel extends JPanel {
    private static final Color ALLOC_COLOR = new Color(41, 128, 185);
    private static final Color REQ_COLOR = new Color(230, 126, 34);
    private static final Color WAIT_COLOR = new Color(192, 57, 43);
    private static final Color NEED_COLOR = new Color(150, 150, 150);
    private static final Stroke SOLID = new BasicStroke(2f);
    private static final Stroke THICK = new BasicStroke(3f);

    static final Color[] PALETTE = {
        new Color(231, 76, 60), new Color(52, 152, 219), new Color(155, 89, 182), new Color(241, 196, 15),
        new Color(26, 188, 156), new Color(230, 126, 34), new Color(127, 140, 141), new Color(46, 204, 113),
        new Color(52, 73, 94), new Color(211, 84, 0)
    };

    private Step step;

    public ResourceGraphPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(640, 500));
    }

    public void setStep(Step step) {
        this.step = step;
        repaint();
    }

    static Color statusColor(Status s) {
        switch (s) {
            case REQUESTING: return new Color(230, 126, 34);
            case CHECKING:   return new Color(241, 196, 15);
            case WAITING:    return new Color(155, 89, 182);
            case FINISHED:   return new Color(39, 174, 96);
            case DEADLOCKED: return new Color(192, 57, 43);
            default:         return new Color(52, 152, 219);
        }
    }

    static String statusText(Status s) {
        switch (s) {
            case REQUESTING: return "хүсэж байна";
            case CHECKING:   return "шалгаж байна";
            case WAITING:    return "хүлээж байна";
            case FINISHED:   return "дууссан";
            case DEADLOCKED: return "DEADLOCK";
            default:         return "ажиллаж байна";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (step == null) return;
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
        int n = step.allocation.length;
        int m = step.available.length;
        int w = getWidth();
        int h = getHeight();
        int top = 52, bottom = 70;
        int usable = Math.max(100, h - top - bottom);

        int nodeR = Math.max(12, Math.min(24, usable / n / 2 - 4));
        Point[] pp = new Point[n];
        Point[] rp = new Point[m];
        for (int i = 0; i < n; i++) pp[i] = new Point((int) (w * 0.24), top + (int) ((i + 0.5) * usable / n));
        for (int j = 0; j < m; j++) rp[j] = new Point((int) (w * 0.78), top + (int) ((j + 0.5) * usable / m));

        int boxW = 120;
        int boxH = Math.min(usable / m - 14, 84);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (step.allocation[i][j] > 0) {
                    boolean lab = n <= 6 || i == step.active || i == step.requester;
                    drawEdge(g2, rp[j], pp[i], boxW / 2, nodeR, lab ? "Alloc " + step.allocation[i][j] : null,
                            ALLOC_COLOR, SOLID, -7, 0.5);
                }
            }
        }
        if (step.active >= 0 && !step.finished[step.active] && step.kind != Step.Kind.DEADLOCK) {
            for (int j = 0; j < m; j++) {
                int nd = step.need[step.active][j];
                if (nd > 0) {
                    drawEdge(g2, pp[step.active], rp[j], nodeR, boxW / 2, "Need " + nd,
                            NEED_COLOR, SOLID, 7, 0.68);
                }
            }
        }
        for (Scenario.Req wq : step.waiting) {
            for (int j = 0; j < m; j++) {
                if (wq.amount()[j] > 0) {
                    drawEdge(g2, pp[wq.process()], rp[j], nodeR, boxW / 2, "хүлээж " + wq.amount()[j],
                            WAIT_COLOR, SOLID, 7, 0.4);
                }
            }
        }
        if (step.request != null && step.requester >= 0) {
            for (int j = 0; j < m; j++) {
                if (step.request[j] > 0) {
                    drawEdge(g2, pp[step.requester], rp[j], nodeR, boxW / 2, "хүсэлт " + step.request[j],
                            REQ_COLOR, THICK, 7, 0.32);
                }
            }
        }

        for (int j = 0; j < m; j++) drawResource(g2, j, rp[j], boxW, boxH);

        for (int i = 0; i < n; i++) drawProcess(g2, i, pp[i], nodeR);

        drawLegend(g2, h);
        drawBadge(g2, w);
    }

    private void drawResource(Graphics2D g2, int j, Point c, int bw, int bh) {
        int x = c.x - bw / 2, y = c.y - bh / 2;
        g2.setColor(new Color(236, 247, 240));
        g2.fillRoundRect(x, y, bw, bh, 12, 12);
        g2.setColor(new Color(39, 174, 96));
        g2.setStroke(SOLID);
        g2.drawRoundRect(x, y, bw, bh, 12, 12);

        g2.setColor(new Color(30, 110, 60));
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("R" + j, x + 8, y + 16);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String info = "бэлэн " + step.available[j] + " / " + step.total[j];
        g2.drawString(info, x + bw - 8 - g2.getFontMetrics().stringWidth(info), y + 16);

        int totalCnt = step.total[j];
        int[] owner = new int[totalCnt];
        int idx = 0;
        for (int i = 0; i < step.allocation.length && idx < totalCnt; i++) {
            for (int k = 0; k < step.allocation[i][j] && idx < totalCnt; k++) owner[idx++] = i;
        }
        int free = Math.max(0, Math.min(step.available[j], totalCnt - idx));
        int perRow = Math.max(5, (bw - 16) / 18);
        int d = 12;
        for (int k = 0; k < totalCnt; k++) {
            int col = k % perRow, row = k / perRow;
            int dx = x + 10 + col * 18;
            int dy = y + 26 + row * 17;
            if (dy + d > y + bh) break;
            if (k < idx) {
                g2.setColor(PALETTE[owner[k] % PALETTE.length]);
                g2.fillOval(dx, dy, d, d);
            } else if (k - idx < free) {
                g2.setColor(Color.WHITE);
                g2.fillOval(dx, dy, d, d);
                g2.setColor(new Color(39, 174, 96));
                g2.drawOval(dx, dy, d, d);
            }
        }
    }

    private void drawProcess(Graphics2D g2, int i, Point p, int r) {
        Status s = step.status[i];
        boolean isActive = i == step.active;
        if (isActive) {
            g2.setColor(new Color(255, 235, 150));
            g2.fillOval(p.x - r - 6, p.y - r - 6, 2 * r + 12, 2 * r + 12);
        }
        Color base = statusColor(s);
        if (s == Status.FINISHED) base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 150);
        g2.setColor(base);
        g2.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
        g2.setColor(PALETTE[i % PALETTE.length]);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);

        g2.setColor(s == Status.CHECKING ? Color.BLACK : Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, r >= 18 ? 14 : 11));
        String name = "P" + i;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(name, p.x - fm.stringWidth(name) / 2, p.y + fm.getAscent() / 2 - 1);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(70, 70, 70));
        String txt = s == Status.FINISHED ? "✅ дууссан" : statusText(s);
        if (step.finishFlags != null && step.finishFlags[i] && s != Status.FINISHED) txt = "✅ дуусаж чадна";
        g2.drawString(txt, p.x - r - 8 - g2.getFontMetrics().stringWidth(txt), p.y + 4);
    }

    private void drawEdge(Graphics2D g2, Point from, Point to, int fromGap, int toGap,
                          String label, Color color, Stroke stroke, double offset, double t) {
        double dx = to.x - from.x, dy = to.y - from.y;
        double len = Math.hypot(dx, dy);
        if (len < fromGap + toGap + 6) return;
        double ux = dx / len, uy = dy / len;
        double nx = -uy * offset, ny = ux * offset;

        double sx = from.x + nx + ux * fromGap * (fromGap > 40 ? 0.9 : 1.0);
        double sy = from.y + ny + uy * fromGap * (fromGap > 40 ? 0.9 : 1.0);
        double ex = to.x + nx - ux * toGap * (toGap > 40 ? 0.9 : 1.0);
        double ey = to.y + ny - uy * toGap * (toGap > 40 ? 0.9 : 1.0);

        g2.setColor(color);
        g2.setStroke(stroke);
        g2.draw(new Line2D.Double(sx, sy, ex, ey));

        g2.setStroke(SOLID);
        double a = 9;
        Path2D head = new Path2D.Double();
        head.moveTo(ex, ey);
        head.lineTo(ex - ux * a - uy * a / 2, ey - uy * a + ux * a / 2);
        head.lineTo(ex - ux * a + uy * a / 2, ey - uy * a - ux * a / 2);
        head.closePath();
        g2.fill(head);

        if (label == null) return;
        double mx = sx + (ex - sx) * t + nx * 0.9;
        double my = sy + (ey - sy) * t + ny * 0.9;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label);
        g2.setColor(new Color(255, 255, 255, 215));
        g2.fillRect((int) mx - tw / 2 - 2, (int) my - fm.getAscent() + 1, tw + 4, fm.getHeight() - 1);
        g2.setColor(color.darker());
        g2.drawString(label, (int) mx - tw / 2, (int) my + 1);
    }

    private void drawLegend(Graphics2D g2, int h) {
        int x = 12, y = h - 62;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        legendLine(g2, x, y, ALLOC_COLOR, SOLID, "Олгосон нөөц (Allocation)");
        legendLine(g2, x, y + 16, REQ_COLOR, THICK, "Одоогийн хүсэлт");
        legendLine(g2, x, y + 32, WAIT_COLOR, SOLID, "Хүлээж буй хүсэлт");
        legendLine(g2, x + 215, y, NEED_COLOR, SOLID, "Дахин авах дээд хэмжээ (Need)");

        int cx = x + 215;
        g2.setColor(Color.WHITE);
        g2.fillOval(cx, y + 20, 11, 11);
        g2.setColor(new Color(39, 174, 96));
        g2.drawOval(cx, y + 20, 11, 11);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Чөлөөтэй нөөцийн нэгж", cx + 34, y + 30);
        g2.setColor(PALETTE[1]);
        g2.fillOval(cx, y + 36, 11, 11);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Процесст олгогдсон нэгж (өнгө = эзэн)", cx + 34, y + 46);
    }

    private void legendLine(Graphics2D g2, int x, int y, Color c, Stroke s, String text) {
        g2.setColor(c);
        g2.setStroke(s);
        g2.drawLine(x, y, x + 26, y);
        g2.setStroke(SOLID);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(text, x + 34, y + 4);
    }

    private void drawBadge(Graphics2D g2, int w) {
        String text;
        Color c;
        switch (step.sys) {
            case SAFE:     text = "● SAFE (аюулгүй)"; c = new Color(39, 174, 96); break;
            case UNSAFE:   text = "● UNSAFE (аюултай)"; c = new Color(230, 126, 34); break;
            case DEADLOCK: text = "● DEADLOCK (түгжрэл)"; c = new Color(192, 57, 43); break;
            default:       text = "● Төлөв шалгагдаагүй"; c = new Color(127, 140, 141);
        }
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int bw = fm.stringWidth(text) + 24;
        int x = w - bw - 12, y = 10;
        g2.setColor(c);
        g2.fillRoundRect(x, y, bw, 30, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x + 12, y + 20);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        String mode = step.bankerOn ? "Banker's Algorithm: АСААЛТТАЙ" : "Banker's Algorithm: УНТРААТАЙ";
        g2.setColor(step.bankerOn ? new Color(39, 174, 96) : new Color(192, 57, 43));
        g2.drawString(mode, 14, 28);
    }
}