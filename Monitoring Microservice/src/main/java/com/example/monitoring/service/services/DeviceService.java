package com.example.monitoring.service.services;

import com.example.monitoring.service.dtos.DeviceDTO;
import com.example.monitoring.service.dtos.builders.DeviceBuilder;
import com.example.monitoring.service.entities.Device;
import com.example.monitoring.service.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional(readOnly = true)
    public DeviceDTO findById(UUID id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new RuntimeException("Device with id: " + id + " was not found in db");
        }
        return DeviceBuilder.toDeviceDTO(device.get());
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> findAll() {
        return deviceRepository.findAll()
                .stream()
                .map(DeviceBuilder::toDeviceDTO)
                .toList();
    }

    @Transactional
    public UUID insert(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.info("Device with id {} was inserted in the database", device.getId());
        return device.getId();
    }

    @Transactional
    public void delete(UUID id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new RuntimeException("Device with id: " + id + " was not found in db");
        }
        deviceRepository.deleteById(id);
        LOGGER.info("Device with id {} was deleted from the database", id);
    }
}
