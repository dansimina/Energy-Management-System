package com.example.monitoring.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "device_data")
public class DeviceData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @NotNull(message = "date is required")
    private LocalDate date;

    @NotNull(message = "time is required")
    private LocalTime time;

    @NotNull(message = "value is required")
    private Integer value;

    @ManyToOne
    private Device device;

    public DeviceData() {}

    public DeviceData(LocalDate date, LocalTime time, Integer value) {
        this.date = date;
        this.time = time;
        this.value = value;
    }

    public DeviceData(UUID id, LocalDate date, LocalTime time, Integer value) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.value = value;
    }

    public DeviceData(LocalDate date, LocalTime time, Integer value, Device device) {
        this.date = date;
        this.time = time;
        this.value = value;
        this.device = device;
    }

    public DeviceData(UUID id, LocalDate date, LocalTime time, Integer value, Device device) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.value = value;
        this.device = device;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", value=" + value +
                ", device=" + device +
                '}';
    }
}
