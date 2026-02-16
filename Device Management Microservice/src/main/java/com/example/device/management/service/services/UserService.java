package com.example.device.management.service.services;

import com.example.device.management.service.dtos.DeviceDTO;
import com.example.device.management.service.dtos.UserDTO;
import com.example.device.management.service.dtos.builders.DeviceBuilder;
import com.example.device.management.service.dtos.builders.UserBuilder;
import com.example.device.management.service.entities.Device;
import com.example.device.management.service.entities.User;
import com.example.device.management.service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.device.management.service.messaging.DeviceAlertMessageType;
import com.example.device.management.service.messaging.MessageProducer;
import com.example.device.management.service.messaging.UserNotificationMessageType;
import com.example.device.management.service.repositories.DeviceRepository;
import com.example.device.management.service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final MessageProducer messageProducer;

    @Autowired
    public UserService(UserRepository userRepository, DeviceRepository deviceRepository, MessageProducer messageProducer) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.messageProducer = messageProducer;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDTO(user.get());
    }

    @Transactional(readOnly = true)
    public List<DeviceDTO> getUserDevices(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id));
        List<Device> devices = user.getDevices();

        if (devices.isEmpty()) {
            return Collections.emptyList();
        }

        return devices.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UUID insert(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.info("User with id {} was inserted in the database", user.getId());
        return user.getId();
    }

    @Transactional
    public void insertUserDevice(UUID userId, UUID deviceId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            LOGGER.error("User with id {} was not found in db", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userId);
        }

        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        device.get().setUser(user.get());
        deviceRepository.save(device.get());
        LOGGER.info("Device with id {} was added to user with id {}", deviceId, userId);
    }

    @Transactional
    public void delete(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        for (Device device : user.get().getDevices()) {
            device.setUser(null);
            deviceRepository.save(device);
        }

        userRepository.deleteById(id);
        LOGGER.info("User with id {} was deleted from the database", id);
    }

    @Transactional
    public void deleteUserDevice(UUID userId, UUID deviceId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            LOGGER.error("User with id {} was not found in db", userId);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userId);
        }

        Optional<Device> device = deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        device.get().setUser(null);
        deviceRepository.save(device.get());

        user.get().getDevices().remove(device.get());
        userRepository.save(user.get());
        LOGGER.info("Device with id {} was removed from user with id {}", deviceId, userId);
    }

    public void notifyUser(UUID deviceId, Integer value, String timestamp) {
        Optional<Device> device = deviceRepository.findById(deviceId);

        if (device.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        User user = device.get().getUser();

        if (user == null) {
            return;
        }

        UserNotificationMessageType message = new UserNotificationMessageType(user.getId(), deviceId, value, timestamp);
        messageProducer.sendToUserNotificationQueue(message);
        LOGGER.info("Sending message to user with id {}: {}", user.getId(), message);
    }
}
