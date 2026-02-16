package com.example.monitoring.service.repositories;

import com.example.monitoring.service.entities.Device;
import com.example.monitoring.service.entities.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceDataRepository extends JpaRepository<DeviceData, UUID> {
    Optional<DeviceData> findByDeviceAndDateAndTime(Device device, LocalDate dateTime, LocalTime time);
    List<DeviceData> findByDevice(Device device);
    List<DeviceData> findByDeviceAndDate(Device device, LocalDate date);
    List<DeviceData> findByDeviceIdInAndDate(List<UUID> deviceIds, LocalDate date);
}
