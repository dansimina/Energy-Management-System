package com.example.monitoring.service.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class DeviceDataDTO {
    private UUID id;
    private LocalDate date;
    private LocalTime time;
    private Integer value;

    public DeviceDataDTO() {
    }

    public DeviceDataDTO(UUID id, LocalDate date, LocalTime time, Integer value) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceDataDTO that = (DeviceDataDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, value);
    }

    @Override
    public String toString() {
        return "DeviceDataDTO{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", value=" + value +
                '}';
    }
}
