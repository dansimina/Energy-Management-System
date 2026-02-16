package com.example.authorization.service.config;

import com.example.authorization.service.common.UserRole;
import com.example.authorization.service.entities.Credentials;
import com.example.authorization.service.repositories.CredentialsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class Initializer {
    public static final UUID ADMIN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin";
    public static final UserRole ADMIN_TYPE = UserRole.ADMIN;

    @Bean
    public CommandLineRunner init(CredentialsRepository credentialsRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            credentialsRepository.findById(ADMIN_UUID).orElseGet(() -> credentialsRepository.save(new Credentials(ADMIN_UUID, ADMIN_USERNAME, passwordEncoder.encode(ADMIN_PASSWORD), ADMIN_TYPE)));
        };
    }
}
