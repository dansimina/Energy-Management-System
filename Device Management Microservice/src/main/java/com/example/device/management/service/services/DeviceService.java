package com.example.device.management.service.services;

import com.example.device.management.service.dtos.DeviceDTO;
import com.example.device.management.service.dtos.builders.DeviceBuilder;
import com.example.device.management.service.entities.Device;
import com.example.device.management.service.entities.User;
import com.example.device.management.service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.device.management.service.messaging.DeviceIdMessageType;
import com.example.device.management.service.messaging.MessageProducer;
import com.example.device.management.service.messaging.OperationType;
import com.example.device.management.service.repositories.DeviceRepository;
import com.example.device.management.service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final MessageProducer messageProducer;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, MessageProducer messageProducer) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.messageProducer = messageProducer;
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> getDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> getUnassignedDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .filter((device -> device.getUser() == null))
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeviceDTO getDevice(UUID id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDTO(device.get());
    }

    @Transactional
    public UUID insert(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());

        DeviceIdMessageType deviceMessage = new DeviceIdMessageType(OperationType.INSERT, device.getId(), device.getMaximumConsumptionValue());
        messageProducer.sendToMonitoringQueue(deviceMessage);

        return device.getId();
    }

    @Transactional
    public DeviceDTO update(UUID id, DeviceDTO deviceDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        Device updated = deviceOptional.get();
        updated.setName(deviceDTO.getName());
        updated.setMaximumConsumptionValue(deviceDTO.getMaximumConsumptionValue());
        updated.setEnergyClass(deviceDTO.getEnergyClass());
        updated.setDescription(deviceDTO.getDescription());
        updated = deviceRepository.save(updated);
        LOGGER.debug("Device with id {} was updated in db", updated.getId());
        return DeviceBuilder.toDeviceDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        User user = device.get().getUser();

        if (user != null) {
            user.getDevices().remove(device.get());
            userRepository.save(user);
        }

        deviceRepository.deleteById(id);
        LOGGER.info("Device with id {} was deleted from the database", id);

        DeviceIdMessageType deviceMessage = new DeviceIdMessageType(OperationType.DELETE, id, null);
        messageProducer.sendToMonitoringQueue(deviceMessage);
    }
}
