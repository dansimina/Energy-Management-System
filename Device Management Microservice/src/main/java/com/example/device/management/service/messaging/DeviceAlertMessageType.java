package com.example.device.management.service.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceAlertMessageType {
    private UUID deviceId;
    private Integer value;
    private String timestamp;

    public DeviceAlertMessageType() {
    }

    public DeviceAlertMessageType(UUID deviceId, Integer value, String timestamp) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public Integer getValue() {
        return value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DeviceAlertMessageType{" + "deviceId=" + deviceId + ", timestamp=" + timestamp + '}';
    }
}
