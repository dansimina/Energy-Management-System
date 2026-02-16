package org.example;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            DataSimulatorGUI gui = new DataSimulatorGUI();
            gui.setVisible(true);
        });
    }
}
