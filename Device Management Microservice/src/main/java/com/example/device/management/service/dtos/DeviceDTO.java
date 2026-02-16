package com.example.device.management.service.dtos;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {
    private UUID id;
    private String name;
    private Integer maximumConsumptionValue;
    private String energyClass;
    private String description;

    public DeviceDTO() {
    }

    public DeviceDTO(String name, Integer maximumConsumptionValue, String energyClass, String description) {
        this.name = name;
        this.maximumConsumptionValue = maximumConsumptionValue;
        this.energyClass = energyClass;
        this.description = description;
    }

    public DeviceDTO(UUID id, String name, Integer maximumConsumptionValue, String energyClass, String description) {
        this.id = id;
        this.name = name;
        this.maximumConsumptionValue = maximumConsumptionValue;
        this.energyClass = energyClass;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaximumConsumptionValue() {
        return maximumConsumptionValue;
    }

    public void setMaximumConsumptionValue(Integer maximumConsumptionValue) {
        this.maximumConsumptionValue = maximumConsumptionValue;
    }

    public String getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(String energyClass) {
        this.energyClass = energyClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return Objects.equals(name, deviceDTO.name) && Objects.equals(maximumConsumptionValue, deviceDTO.maximumConsumptionValue) && Objects.equals(energyClass, deviceDTO.energyClass) && Objects.equals(description, deviceDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maximumConsumptionValue, energyClass, description);
    }

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maximumConsumptionValue=" + maximumConsumptionValue +
                ", energyClass='" + energyClass + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
