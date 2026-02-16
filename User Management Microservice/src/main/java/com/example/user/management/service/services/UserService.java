package com.example.user.management.service.services;


import com.example.user.management.service.dtos.UserDTO;
import com.example.user.management.service.dtos.builders.UserBuilder;
import com.example.user.management.service.entities.User;
import com.example.user.management.service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.user.management.service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO findUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDTO(user.get());
    }

    @Transactional
    public UUID insert(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());

        return user.getId();
    }

    @Transactional
    public UserDTO update(UUID id, UserDTO userDTO) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        User updated = user.get();
        updated.setFirstName(userDTO.getFirstName());
        updated.setLastName(userDTO.getLastName());
        updated.setEmail(userDTO.getEmail());
        updated.setAddress(userDTO.getAddress());
        updated.setAge(userDTO.getAge());
        updated = userRepository.save(updated);
        LOGGER.debug("User with id {} was updated in db", updated.getId());
        return UserBuilder.toUserDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
