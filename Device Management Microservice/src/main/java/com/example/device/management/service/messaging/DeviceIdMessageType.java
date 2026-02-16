package com.example.device.management.service.messaging;

import java.util.UUID;

public class DeviceIdMessageType {
    private OperationType type;
    private UUID id;
    private Integer maximumConsumptionValue;

    public DeviceIdMessageType() {
    }

    public DeviceIdMessageType(OperationType type, UUID id, Integer maximumConsumptionValue) {
        this.type = type;
        this.id = id;
        this.maximumConsumptionValue = maximumConsumptionValue;
    }

    public OperationType getType() {
        return type;
    }
    public void setType(OperationType type) {
        this.type = type;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Integer getMaximumConsumptionValue() {
        return maximumConsumptionValue;
    }
    public void setMaximumConsumptionValue(Integer maximumConsumptionValue) {
        this.maximumConsumptionValue = maximumConsumptionValue;
    }

    @Override
    public String toString() {
        return "DeviceIdMessageType{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }
}
