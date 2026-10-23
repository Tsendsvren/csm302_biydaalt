package com.os;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("II-4-13: Banker's Algorithm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 880);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);
        setContentPane(new SimulatorPanel());
    }
}