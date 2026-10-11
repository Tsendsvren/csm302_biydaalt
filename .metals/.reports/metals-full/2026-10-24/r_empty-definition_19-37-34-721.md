error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/CodeViewerPanel.java:com/os/Step/Code#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/CodeViewerPanel.java
empty definition using pc, found symbol in pc: com/os/Step/Code#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 36
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/CodeViewerPanel.java
text:
```scala
package com.os;

import com.os.Step.@@Code;

import javax.swing.*;
import java.awt.*;

/** Алгоритмын энгийн Монгол псевдокод. Одоо ажиллаж буй мөр тодорно. */
public class CodeViewerPanel extends JPanel {
    private static final String[] REQUEST = {
        "Request ≤ Need мөн үү?   (үгүй бол → АЛДАА: хэт их хүсэлт)",
        "Request ≤ Available мөн үү?   (үгүй бол → ХҮЛЭЭНЭ)",
        "Түр өгч үз: Available −= Request, Allocation += Request, Need −= Request",
        "Safety Algorithm-аар систем SAFE эсэхийг шалга",
        "SAFE бол → нөөцийг ӨГНӨ",
        "UNSAFE бол → түр өгснийг БУЦААЖ, процесс ХҮЛЭЭНЭ"
    };
    private static final String[] SAFETY = {
        "Work = Available;  Finish[i] = false (бүгд)",
        "Finish[i] = false ба Need[i] ≤ Work байх i-г ол",
        "Олдвол: Work += Allocation[i];  Finish[i] = true;  2-р мөр рүү буц",
        "Олдохгүй бол → 5-р мөр рүү",
        "Бүх Finish[i] = true бол SAFE, үгүй бол UNSAFE"
    };
    private static final String[] DETECT = {
        "Work = Available;  (хэрэгцээгүй бол Finish[i] = true)",
        "Finish[i] = false ба Request[i] ≤ Work байх i-г ол",
        "Олдвол: Work += Allocation[i];  Finish[i] = true;  2-р мөр рүү буц",
        "Олдохгүй бол → 5-р мөр рүү",
        "Finish[i] = false үлдсэн бол тэр процесс DEADLOCK-д орсон"
    };

    private Code block = Code.REQUEST;
    private int line = -1;

    public CodeViewerPanel() {
        setBackground(new Color(252, 252, 250));
    }

    public void setStep(Step s) {
        if (s.code != Code.NONE) block = s.code;
        line = s.code == Code.NONE ? -1 : s.codeLine;
        repaint();
    }

    private String title() {
        switch (block) {
            case SAFETY: return "Safety Algorithm (систем аюулгүй эсэх)";
            case DETECT: return "Deadlock Detection (түгжрэл илрүүлэх)";
            default:     return "Resource-Request Algorithm (хүсэлт шалгах)";
        }
    }

    private String[] lines() {
        switch (block) {
            case SAFETY: return SAFETY;
            case DETECT: return DETECT;
            default:     return REQUEST;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            g2.setColor(new Color(44, 62, 80));
            g2.fillRect(0, 0, w, 26);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("📖 " + title(), 10, 17);

            String[] ls = lines();
            int rh = 22;
            int y = 30;
            for (int i = 0; i < ls.length; i++) {
                boolean on = (i + 1) == line;
                if (on) {
                    g2.setColor(new Color(41, 128, 185));
                    g2.fillRect(0, y, w, rh);
                }
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2.setColor(on ? new Color(200, 225, 245) : Color.GRAY);
                g2.drawString(String.valueOf(i + 1), 10, y + 15);
                g2.setFont(new Font("SansSerif", on ? Font.BOLD : Font.PLAIN, 12));
                g2.setColor(on ? Color.WHITE : new Color(40, 40, 40));
                g2.drawString(ls[i], 32, y + 15);
                y += rh;
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(520, 30 + 6 * 22 + 6);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: com/os/Step/Code#