package com.example.monitoring.service.services;

import com.example.monitoring.service.dtos.DeviceDataDTO;
import com.example.monitoring.service.dtos.HourlyConsumptionDTO;
import com.example.monitoring.service.dtos.builders.DeviceDataBuilder;
import com.example.monitoring.service.entities.Device;
import com.example.monitoring.service.entities.DeviceData;
import com.example.monitoring.service.messaging.DeviceAlertMessageType;
import com.example.monitoring.service.messaging.MessageProducer;
import com.example.monitoring.service.repositories.DeviceDataRepository;
import com.example.monitoring.service.repositories.DeviceRepository;
import com.zaxxer.hikari.util.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Credentials.class);
    private final DeviceRepository deviceRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final MessageProducer messageProducer;

    @Value("${simulator.delay.ms}")
    private long SIMULATOR_DELAY_MS;

    public DeviceDataService(DeviceRepository deviceRepository, DeviceDataRepository deviceDataRepository, MessageProducer messageProducer) {
        this.deviceRepository = deviceRepository;
        this.deviceDataRepository = deviceDataRepository;
        this.messageProducer = messageProducer;
    }

    @Transactional
    public UUID insert(UUID deviceId, DeviceDataDTO deviceDataDTO) {
        DeviceData deviceData = DeviceDataBuilder.toEntity(deviceDataDTO);
        deviceData.setDevice(deviceRepository.findById(deviceId).orElseThrow());
        return deviceDataRepository.save(deviceData).getId();
    }

    @Transactional
    public UUID insert(UUID deviceId, String timestamp, Integer value) {
        if (SIMULATOR_DELAY_MS > 0) {
            try {
                LOGGER.info("Processing with delay: {} ms", SIMULATOR_DELAY_MS);
                Thread.sleep(SIMULATOR_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        LOGGER.debug("Inserting data for device {} at {}", deviceId, timestamp);
        LocalDateTime dateTime = LocalDateTime.parse(timestamp);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = LocalTime.of(dateTime.getHour(), 0);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device with id: " + deviceId + " was not found in db"));

        LOGGER.debug("Device {} found in db", device.getId());

        // Query the database directly instead of loading all data
        Optional<DeviceData> existingData = deviceDataRepository
                .findByDeviceAndDateAndTime(device, date, time);

        LOGGER.debug("Existing data for device {} at {}T{}: {}", device.getId(), date, time, existingData);

        DeviceData deviceData;
        if (existingData.isPresent()) {
            deviceData = existingData.get();
            deviceData.setValue(deviceData.getValue() + value);

            LOGGER.debug("Updated data for device {} at {}T{}: {}", device.getId(), date, time, deviceData);
        } else {
            deviceData = new DeviceData(date, time, value, device);
        }

        UUID id = deviceDataRepository.save(deviceData).getId();

        try {
            if (deviceData.getValue() > device.getMaximumConsumptionValue()) {
                DeviceAlertMessageType alertMessage = new DeviceAlertMessageType(deviceId, deviceData.getValue(), timestamp);
                messageProducer.sendDeviceAlert(alertMessage);
            }
        } catch (Exception e) {
            LOGGER.error("Error while sending message", e);
        }

        return id;
    }

    @Transactional(readOnly = true)
    public List<DeviceDataDTO> findByDevice(UUID deviceId) {
        Optional<Device> device = deviceRepository.findById(deviceId);

        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new RuntimeException("Device with id: " + deviceId + " was not found in db");
        }

        List<DeviceData> deviceDataList = deviceDataRepository.findByDevice(device.get());

        return deviceDataList.stream()
                .map(DeviceDataBuilder::toDeviceDataDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceDataDTO> findByDeviceAndDate(UUID deviceId, LocalDate date) {
        Optional<Device> device = deviceRepository.findById(deviceId);

        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new RuntimeException("Device with id: " + deviceId + " was not found in db");
        }

        List<DeviceData> deviceDataList = deviceDataRepository.findByDeviceAndDate(device.get(), date);
        return deviceDataList.stream()
                .map(DeviceDataBuilder::toDeviceDataDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HourlyConsumptionDTO> getHourlyConsumptionForDevices(List<UUID> deviceIds, LocalDate date) {
        List<HourlyConsumptionDTO> consumption = initializeEmptyHours();

        if (deviceIds == null || deviceIds.isEmpty()) {
            return consumption;
        }

        List<DeviceData> allData = deviceDataRepository.findByDeviceIdInAndDate(deviceIds, date);

        for (DeviceData deviceData : allData) {
            int hour = deviceData.getTime().getHour();
            if (hour >= 0 && hour < 24) {
                consumption.get(hour).setValue(
                        consumption.get(hour).getValue() + deviceData.getValue()
                );
            }
        }

        return consumption;
    }

    private List<HourlyConsumptionDTO> initializeEmptyHours() {
        List<HourlyConsumptionDTO> consumption = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            consumption.add(new HourlyConsumptionDTO(LocalTime.of(hour, 0), 0));
        }
        return consumption;
    }
}
