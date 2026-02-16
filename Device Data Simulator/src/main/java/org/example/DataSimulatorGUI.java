package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSimulatorGUI extends JFrame {
    private static final int NUM_DEVICES = 4;
    private static final String DEFAULT_UUID = "00000000-0000-0000-0000-000000000000";

    private List<JTextField> deviceIdFields;
    private List<JTextField> maxConsumptionFields;
    private JTextField dateField;
    private JComboBox<String> timeIntervalComboBox;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;

    private ExecutorService executorService;
    private List<DataSimulator> simulators;
    private volatile boolean running = false;

    public DataSimulatorGUI() {
        setTitle("Energy Consumption Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 700);
        setLocationRelativeTo(null);

        deviceIdFields = new ArrayList<>();
        maxConsumptionFields = new ArrayList<>();
        simulators = new ArrayList<>();

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createConfigPanel(), BorderLayout.CENTER);
        contentPanel.add(createLogPanel(), BorderLayout.SOUTH);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JLabel title = new JLabel("Energy Consumption Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));

        panel.add(title);
        return panel;
    }

    private JPanel createConfigPanel() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        int row = 0;

        // Device sections
        for (int i = 0; i < NUM_DEVICES; i++) {
            row = addDeviceRow(card, gbc, row, i + 1);
        }

        // Separator
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 15, 8);
        JSeparator separator = new JSeparator();
        card.add(separator, gbc);
        row++;

        // Reset insets
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.gridwidth = 1;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        card.add(createLabel("Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateField = createTextField(currentDate);
        card.add(dateField, gbc);
        row++;

        // Time Interval
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        card.add(createLabel("Time Interval (ms):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        timeIntervalComboBox = createTimeIntervalComboBox();
        card.add(timeIntervalComboBox, gbc);
        row++;

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setPreferredSize(new Dimension(160, 40));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startSimulation());

        stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setBorderPainted(false);
        stopButton.setPreferredSize(new Dimension(100, 40));
        stopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopSimulation());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        card.add(buttonPanel, gbc);

        return card;
    }

    private int addDeviceRow(JPanel card, GridBagConstraints gbc, int row, int deviceNum) {
        Color[] colors = {
                new Color(46, 204, 113),   // Green
                new Color(52, 152, 219),   // Blue
                new Color(155, 89, 182),   // Purple
                new Color(230, 126, 34)    // Orange
        };
        Color color = colors[deviceNum - 1];

        // Device header
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel deviceHeader = new JLabel("Device " + deviceNum);
        deviceHeader.setFont(new Font("Arial", Font.BOLD, 13));
        deviceHeader.setForeground(color);
        card.add(deviceHeader, gbc);
        row++;

        gbc.gridwidth = 1;

        // Device ID
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        card.add(createLabel("    Device ID:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField idField = createTextField(DEFAULT_UUID);
        idField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        deviceIdFields.add(idField);
        card.add(idField, gbc);
        row++;

        // Max Consumption
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        card.add(createLabel("    Max Consumption (W/h):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField maxField = createTextField("0");
        maxConsumptionFields.add(maxField);
        card.add(maxField, gbc);
        row++;

        return row;
    }

    private JComboBox<String> createTimeIntervalComboBox() {
        String[] intervals = {
                "0 ms (max speed)", "5 ms", "10 ms", "50 ms", "100 ms",
                "200 ms", "300 ms", "500 ms", "1000 ms"
        };

        JComboBox<String> comboBox = new JComboBox<>(intervals);
        comboBox.setSelectedItem("5 ms");
        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(300, 32));
        comboBox.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        return comboBox;
    }

    private JPanel createLogPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(0, 200));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel logTitle = new JLabel("Console");
        logTitle.setFont(new Font("Arial", Font.BOLD, 14));
        logTitle.setForeground(new Color(52, 73, 94));

        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearButton.addActionListener(e -> logArea.setText(""));

        headerPanel.add(logTitle, BorderLayout.WEST);
        headerPanel.add(clearButton, BorderLayout.EAST);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(40, 44, 52));
        logArea.setForeground(new Color(171, 178, 191));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200), 1));

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(300, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void startSimulation() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            String selectedInterval = (String) timeIntervalComboBox.getSelectedItem();
            int timeInterval;
            if (selectedInterval.contains("max speed")) {
                timeInterval = 0;
            } else {
                timeInterval = Integer.parseInt(selectedInterval.replace(" ms", ""));
            }

            // Validate all device IDs
            for (int i = 0; i < NUM_DEVICES; i++) {
                UUID.fromString(deviceIdFields.get(i).getText().trim());
                Integer.parseInt(maxConsumptionFields.get(i).getText().trim());
            }

            running = true;
            simulators.clear();
            setUIEnabled(false);

            logArea.setText("");
            log("Starting simulation...");
            log("Date: " + date);
            log("Interval: " + (timeInterval == 0 ? "max speed" : timeInterval + " ms"));
            log("Measurements per device: 144 (24h × 6 per hour)");
            log("Total measurements: " + (NUM_DEVICES * 144));
            log("------------------------");

            executorService = Executors.newFixedThreadPool(NUM_DEVICES);
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < NUM_DEVICES; i++) {
                final int deviceNum = i + 1;
                final String deviceName = "Device-" + deviceNum;
                final UUID deviceId = UUID.fromString(deviceIdFields.get(i).getText().trim());
                final int maxConsumption = Integer.parseInt(maxConsumptionFields.get(i).getText().trim());

                log("[" + deviceName + "] Starting...");
                log("[" + deviceName + "] ID: " + deviceId);
                log("[" + deviceName + "] Max: " + maxConsumption + " W/h");

                DataSimulator simulator = new DataSimulator(
                        deviceId, date, maxConsumption, timeInterval,
                        this::log, deviceName
                );
                simulators.add(simulator);
                executorService.submit(simulator::start);
            }

            log("------------------------");

            // Monitor completion
            new Thread(() -> {
                executorService.shutdown();
                while (!executorService.isTerminated() && running) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                int totalMessages = NUM_DEVICES * 144;
                double rate = totalMessages / (duration / 1000.0);

                SwingUtilities.invokeLater(() -> {
                    log("------------------------");
                    log("Simulation completed!");
                    log("Duration: " + String.format("%.2f", duration / 1000.0) + " seconds");
                    log("Total sent: " + totalMessages + " measurements");
                    log("Throughput: " + String.format("%.0f", rate) + " msg/sec");
                    log("------------------------");
                    resetUI();
                });
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            resetUI();
        }
    }

    private void stopSimulation() {
        running = false;
        for (DataSimulator sim : simulators) {
            sim.stop();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log("------------------------");
        log("Simulation stopped by user.");
        log("------------------------");
        resetUI();
    }

    private void setUIEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
        stopButton.setEnabled(!enabled);
        dateField.setEnabled(enabled);
        timeIntervalComboBox.setEnabled(enabled);
        for (JTextField field : deviceIdFields) {
            field.setEnabled(enabled);
        }
        for (JTextField field : maxConsumptionFields) {
            field.setEnabled(enabled);
        }
    }

    private void resetUI() {
        running = false;
        setUIEnabled(true);
    }
}