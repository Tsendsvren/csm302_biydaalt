error id: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ExplainPanel.java:com/os/Step/Check#
file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ExplainPanel.java
empty definition using pc, found symbol in pc: com/os/Step/Check#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 36
uri: file:///C:/Users/User/Documents/2026/csm302/II-4-13-Bankers-Algorithm/src/main/java/com/os/ExplainPanel.java
text:
```scala
package com.os;

import com.os.Step.@@Check;

import javax.swing.*;
import java.awt.*;

/** Одоогийн алхмыг ямар ч мэдлэггүй хүнд ойлгомжтой энгийн үгээр тайлбарлах карт. */
public class ExplainPanel extends JPanel {
    private final JEditorPane pane = new JEditorPane("text/html", "");

    public ExplainPanel() {
        setLayout(new BorderLayout());
        pane.setEditable(false);
        pane.setBackground(Color.WHITE);
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        add(new JScrollPane(pane), BorderLayout.CENTER);
    }

    private static String color(Step.Kind k) {
        switch (k) {
            case GRANT: case FINISH: case DONE: return "#1e8449";
            case WAIT: return "#8e44ad";
            case DENY: case ERROR: case DEADLOCK: return "#c0392b";
            case REQUEST: return "#d35400";
            case SAFETY: case DETECT: case CHECK: return "#2471a3";
            default: return "#2c3e50";
        }
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    public void setStep(Step s, int index, int count) {
        StringBuilder h = new StringBuilder();
        h.append("<html><body style='font-family:SansSerif; margin:6px 10px;'>");
        h.append("<div style='font-size:12pt; color:#888888;'>Алхам ").append(index + 1).append(" / ").append(count).append("</div>");
        h.append("<div style='font-size:17pt; font-weight:bold; color:").append(color(s.kind)).append(";'>")
                .append(esc(s.title)).append("</div>");
        h.append("<div style='font-size:13pt; color:#222222; margin-top:4px;'>").append(esc(s.text)).append("</div>");
        if (!s.checks.isEmpty()) {
            h.append("<div style='font-size:12pt; margin-top:6px;'>");
            for (Check c : s.checks) {
                String icon = c.ok() == null ? "⏳" : c.ok() ? "✅" : "❌";
                String col = c.ok() == null ? "#555555" : c.ok() ? "#1e8449" : "#c0392b";
                h.append("<div style='color:").append(col).append(";'>").append(icon).append(" ")
                        .append(esc(c.label())).append("</div>");
            }
            h.append("</div>");
        }
        h.append("</body></html>");
        pane.setText(h.toString());
        pane.setCaretPosition(0);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: com/os/Step/Check#