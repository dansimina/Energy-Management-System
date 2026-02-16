package com.example.user.management.service.config;

import com.example.user.management.service.entities.User;
import com.example.user.management.service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class Initializer {
    public static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final String ADMIN_FIRST_NAME = "admin";
    public static final String ADMIN_LAST_NAME = "admin";
    public static final String ADMIN_EMAIL = "admin@email.com";
    public static final String ADMIN_ADDRESS = "admin address";
    public static final int ADMIN_AGE = 99;
    @Bean
    public CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            userRepository.findById(ADMIN_ID).orElseGet(() -> userRepository.save(new User(ADMIN_ID, ADMIN_FIRST_NAME, ADMIN_LAST_NAME,  ADMIN_EMAIL, ADMIN_ADDRESS, ADMIN_AGE)));
        };
    }
}
