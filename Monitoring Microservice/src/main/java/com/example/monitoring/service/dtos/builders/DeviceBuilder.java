package com.example.monitoring.service.dtos.builders;

import com.example.monitoring.service.dtos.DeviceDTO;
import com.example.monitoring.service.entities.Device;

public class DeviceBuilder {
    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getMaximumConsumptionValue());
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(deviceDTO.getId(), deviceDTO.getMaximumConsumptionValue());
    }
}
