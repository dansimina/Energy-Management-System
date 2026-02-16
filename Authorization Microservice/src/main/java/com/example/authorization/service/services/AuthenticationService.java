package com.example.authorization.service.services;

import com.example.authorization.service.common.UserRole;
import com.example.authorization.service.config.Initializer;
import com.example.authorization.service.config.JwtService;
import com.example.authorization.service.dtos.*;
import com.example.authorization.service.entities.Credentials;
import com.example.authorization.service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.authorization.service.messaging.MessageProducer;
import com.example.authorization.service.messaging.OperationType;
import com.example.authorization.service.messaging.UserIdMessageType;
import com.example.authorization.service.messaging.UserMessageType;
import com.example.authorization.service.repositories.CredentialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Credentials.class);

    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessageProducer messageProducer;

    @Autowired
    public AuthenticationService(CredentialsRepository credentialsRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, MessageProducer messageProducer) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.messageProducer = messageProducer;
    }

    @Transactional(readOnly = true)
    public UserCredentialsDTO findUserCredentialById(UUID id){
        Credentials credentials = credentialsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Credentials.class.getSimpleName() + " with id: " + id));
        return new UserCredentialsDTO(credentials.getUsername(), null, credentials.getRole());
    }

    @Transactional
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getUsername(),
                        authRequestDTO.getPassword()
                )
        );
        Credentials credentials = credentialsRepository.findByUsername(authRequestDTO.getUsername()).orElseThrow(() -> new ResourceNotFoundException(Credentials.class.getSimpleName() + " with username: " + authRequestDTO));
        var token = jwtService.generateToken(credentials);
        return new AuthenticationResponseDTO(token, jwtService.extractUserId(token).toString(), jwtService.extractUsername(token), jwtService.extractRole(token));
    }

    @Transactional
    public AuthenticationResponseDTO insert(RegisterRequestDTO registerRequestDTO, Boolean isRegistering) {
        UserCredentialsDTO userCredentialsDTO = registerRequestDTO.getCredentials();
        UserDTO userDTO = registerRequestDTO.getUser();

        if (credentialsRepository.findByUsername(userCredentialsDTO.getUsername()).isPresent()) {
            LOGGER.error("Credentials with username {} was already registered", userCredentialsDTO.getUsername());
            throw new ResourceNotFoundException(Credentials.class.getSimpleName() + " with username: " + userCredentialsDTO);
        }

        UserRole role = isRegistering ? UserRole.USER : registerRequestDTO.getCredentials().getRole();
        Credentials credentials = new Credentials(userCredentialsDTO.getUsername(), passwordEncoder.encode(userCredentialsDTO.getPassword()), role);
        credentials = credentialsRepository.save(credentials);
        LOGGER.debug("Credentials with id {} was inserted in db", credentials.getId());

        UUID id = credentials.getId();;

        var token = jwtService.generateToken(credentials);

        UserMessageType userMessage = new UserMessageType(OperationType.INSERT, id, userDTO);
        messageProducer.sendToUserQueue(userMessage);

        UserIdMessageType userIdMessage = new UserIdMessageType(OperationType.INSERT, id);
        messageProducer.sendToDeviceQueue(userIdMessage);

        return new AuthenticationResponseDTO(token, jwtService.extractUserId(token).toString(), jwtService.extractUsername(token), jwtService.extractRole(token));
    }

    @Transactional
    public void update(UUID id, UpdateUserCredentialsDTO userCredentialsDTO) {
        Optional<Credentials> credentials = credentialsRepository.findById(id);
        if(credentials.isEmpty()) {
            LOGGER.error("Credentials with id {} was not found in db", id);
            throw new ResourceNotFoundException(Credentials.class.getSimpleName() + " with id: " + id);
        }

        if(userCredentialsDTO.getUsername() != null && !userCredentialsDTO.getUsername().isEmpty()) {
            credentials.get().setUsername(userCredentialsDTO.getUsername());
        }
        if(userCredentialsDTO.getPassword() != null && !userCredentialsDTO.getPassword().isEmpty()) {
            credentials.get().setPassword(passwordEncoder.encode(userCredentialsDTO.getPassword()));
        }
        credentials.get().setRole(userCredentialsDTO.getRole());

        credentialsRepository.save(credentials.get());
        LOGGER.debug("Credentials with id {} was updated in db", id);
    }

    @Transactional
    public void delete(UUID id) {
        if(id.equals(Initializer.ADMIN_UUID)) {
            LOGGER.error("Credentials with id {} cannot be deleted", id);
            throw new RuntimeException(Credentials.class.getSimpleName() + " with id: " + id);
        }

        Optional<Credentials> credentials = credentialsRepository.findById(id);
        if(credentials.isEmpty()) {
            LOGGER.error("Credentials with id {} was not found in db", id);
            throw new ResourceNotFoundException(Credentials.class.getSimpleName() + " with id: " + id);
        }
        credentialsRepository.deleteById(id);

        UserMessageType userMessage = new UserMessageType(OperationType.DELETE, id, null);
        messageProducer.sendToUserQueue(userMessage);

        UserIdMessageType deviceMessage = new UserIdMessageType(OperationType.DELETE, id);
        messageProducer.sendToDeviceQueue(deviceMessage);
        LOGGER.info("DELETE messages sent for user ID: {}", id);
    }
}
