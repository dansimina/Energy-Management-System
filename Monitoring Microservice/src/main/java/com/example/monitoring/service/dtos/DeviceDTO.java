package com.example.monitoring.service.dtos;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {
    private UUID id;
    private Integer maximumConsumptionValue;

    public DeviceDTO() {}

    public DeviceDTO(UUID id, Integer maximumConsumptionValue) {
        this.id = id;
        this.maximumConsumptionValue = maximumConsumptionValue;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return id.equals(deviceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                '}';
    }
}
