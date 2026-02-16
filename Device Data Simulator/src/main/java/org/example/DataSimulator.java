package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class DataSimulator {
    private static final String QUEUE_NAME = "external.data.collection.queue";
    private static final String HOST = "localhost";
    private static final int PORT = 5672;
    private static final String USERNAME = "simulator-user";
    private static final String PASSWORD = "simulator-pass";

    private final UUID deviceId;
    private final LocalDate date;
    private final Integer maximumConsumptionValue;
    private final Integer delayMs;
    private final Random random;
    private final ObjectMapper objectMapper;
    private final Consumer<String> logCallback;
    private final String deviceName;
    private volatile boolean running = true;

    public DataSimulator(UUID deviceId, LocalDate date, Integer maximumConsumptionValue, Integer delayMs) {
        this(deviceId, date, maximumConsumptionValue, delayMs, null, null);
    }

    public DataSimulator(UUID deviceId, LocalDate date, Integer maximumConsumptionValue, Integer delayMs, Consumer<String> logCallback, String deviceName) {
        this.deviceId = deviceId;
        this.date = date;
        this.maximumConsumptionValue = maximumConsumptionValue;
        this.delayMs = delayMs;
        this.random = new Random();
        this.objectMapper = new ObjectMapper();
        this.logCallback = logCallback;
        this.deviceName = deviceName != null ? deviceName : "Device";
    }

    public void stop() {
        this.running = false;
    }

    private void log(String message) {
        String formattedMessage = "[" + deviceName + "] " + message;
        System.out.println(formattedMessage);
        if (logCallback != null) {
            logCallback.accept(formattedMessage);
        }
    }

    public void start() {
        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setPort(PORT);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);

            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            log("Connected to RabbitMQ");

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().contentType("application/json").deliveryMode(2).priority(0).build();

            int sentCount = 0;
            int totalMeasurements = 24 * 6; // 144 measurements per day

            for (int hour = 0; hour < 24 && running; hour++) {
                for (int minute = 0; minute < 60 && running; minute += 10) {
                    try {
                        LocalDateTime timestamp = LocalDateTime.of(date, LocalTime.of(hour, minute));

                        Map<String, Object> measurement = new HashMap<>();
                        measurement.put("deviceId", deviceId.toString());
                        measurement.put("timestamp", timestamp.toString());
                        measurement.put("value", generateRealisticConsumption(hour));

                        String message = objectMapper.writeValueAsString(measurement);
                        channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
                        sentCount++;

                        log("Sent: " + timestamp.toLocalTime() + " -> " + measurement.get("value") + "W");

                        if (delayMs > 0) {
                            Thread.sleep(delayMs);
                        }

                    } catch (InterruptedException e) {
                        log("Interrupted");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            log("Completed!  Sent " + sentCount + "/" + totalMeasurements + " measurements");

        } catch (IOException | TimeoutException e) {
            log("ERROR: " + e.getMessage());
        } finally {
            try {
                if (channel != null) channel.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }

    private int generateRealisticConsumption(int hour) {
        double maxPer10Min = maximumConsumptionValue / 6.0;
        double percentage;
        double variance = random.nextDouble();
        boolean isPeakHour = false;

        if (hour >= 0 && hour < 6) {
            percentage = 0.05 + variance * 0.10;
        } else if (hour >= 6 && hour < 8) {
            percentage = 0.25 + variance * 0.25;
        } else if (hour >= 8 && hour < 9) {
            percentage = 0.55 + variance * 0.25;
        } else if (hour >= 9 && hour < 12) {
            percentage = 0.65 + variance * 0.30;
            isPeakHour = true;
        } else if (hour >= 12 && hour < 14) {
            percentage = 0.50 + variance * 0.30;
        } else if (hour >= 14 && hour < 17) {
            percentage = 0.60 + variance * 0.35;
            isPeakHour = true;
        } else if (hour >= 17 && hour < 19) {
            percentage = 0.80 + variance * 0.40;
            isPeakHour = true;
        } else if (hour >= 19 && hour < 21) {
            percentage = 0.70 + variance * 0.30;
        } else {
            percentage = 0.30 + variance * 0.25;
        }

        double consumption = maxPer10Min * percentage;

        if (isPeakHour && variance > 0.7) {
            double overage = maxPer10Min * 0.20 * random.nextDouble();
            consumption += overage;
        }

        return Math.toIntExact(Math.round(consumption));
    }
}