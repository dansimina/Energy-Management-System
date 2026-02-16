package com.example.monitoring.service.dtos.builders;

import com.example.monitoring.service.dtos.DeviceDataDTO;
import com.example.monitoring.service.entities.DeviceData;

public class DeviceDataBuilder {
    private DeviceDataBuilder() {
    }

    public static DeviceDataDTO toDeviceDataDTO(DeviceData deviceData) {
        return new DeviceDataDTO(deviceData.getId(), deviceData.getDate(), deviceData.getTime(), deviceData.getValue());
    }

    public static DeviceData toEntity(DeviceDataDTO deviceDataDTO) {
        return new DeviceData(
            deviceDataDTO.getId(),
            deviceDataDTO.getDate(),
            deviceDataDTO.getTime(),
            deviceDataDTO.getValue()
        );
    }
}