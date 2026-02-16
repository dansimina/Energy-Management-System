package com.example.monitoring.service.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class HourlyConsumptionDTO {
    @NotNull( message = "The value cannot be null" )
    private LocalTime time;

    @NotNull( message = "The value cannot be null" )
    private Integer value;

    public HourlyConsumptionDTO() {
    }

    public HourlyConsumptionDTO(LocalTime time, Integer value) {
        this.time = time;
        this.value = value;
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
    public String toString() {
        return "HourlyConsumptionDTO{" +
                "time=" + time +
                ", value=" + value +
                '}';
    }
}
