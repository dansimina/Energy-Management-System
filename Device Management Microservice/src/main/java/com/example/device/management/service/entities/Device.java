package com.example.device.management.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
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
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "name is required")
    private String name;

    @Column(name = "maximum_consumption_value", nullable = false)
    @NotNull(message = "maximum consumption value is required")
    private Integer maximumConsumptionValue;

    @Column(name = "energy_class", nullable = false)
    @NotBlank(message = "energy class is required")
    private String energyClass;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "description is required")
    private String description;

    @ManyToOne
    private User user;

    public Device() {}
    public Device(String name, Integer maximumConsumptionValue, String energyClass, String description) {
        this.name = name;
        this.maximumConsumptionValue = maximumConsumptionValue;
        this.energyClass = energyClass;
        this.description = description;
    }

    public Device(UUID id, String name, Integer maximumConsumptionValue, String energyClass, String description) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
