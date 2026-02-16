package com.example.device.management.service.dtos.builders;

import com.example.device.management.service.dtos.DeviceDTO;
import com.example.device.management.service.entities.Device;

public class DeviceBuilder {
    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getName(), device.getMaximumConsumptionValue(), device.getEnergyClass(), device.getDescription());
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(deviceDTO.getName(), deviceDTO.getMaximumConsumptionValue(), deviceDTO.getEnergyClass(), deviceDTO.getDescription());
    }
}
