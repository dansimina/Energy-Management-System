package com.example.device.management.service.messaging;

import java.util.UUID;

public class UserNotificationMessageType {
    UUID userId;
    UUID deviceId;
    Integer value;
    String timestamp;

    public UserNotificationMessageType() {
    }

    public UserNotificationMessageType(UUID userId, UUID deviceId, Integer value, String timestamp) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = timestamp;
    }

    public UUID getUserId() {
        return userId;
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

    public void setUserId(UUID userId) {
        this.userId = userId;
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
        return "UserNotificationMessageType{" + "userId=" + userId + ", deviceId=" + deviceId + ", value=" + value + ", timestamp=" + timestamp + '}';
    }
}
