package com.example.monitoring.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @NotNull(message = "id is required")
    private UUID id;

    @NotNull(message = "max consumption value is required")
    private Integer maximumConsumptionValue;

    @OneToMany(mappedBy = "device", orphanRemoval = true)
    private List<DeviceData> data;

    public Device() {}

    public Device(UUID id, Integer maximumConsumptionValue) {
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

    public List<DeviceData> getData() {
        return data;
    }

    public void setData(List<DeviceData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Device [id=" + id + "]";
    }
}
