package org.example.loadbalancingmicroservice.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public class MeasurementMessageType {
    private UUID deviceId;
    private String timestamp;
    private Integer value;

    public MeasurementMessageType() {
    }

    public MeasurementMessageType(UUID deviceId, String timestamp, Integer value) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.value = value;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MeasurementMessage{" +
                "deviceId='" + deviceId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", value=" + value +
                '}';
    }
}
