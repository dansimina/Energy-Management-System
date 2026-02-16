package com.example.device.management.service.config;

import com.example.device.management.service.entities.User;
import com.example.device.management.service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class Initializer {
    public static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    @Bean
    public CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            userRepository.findById(ADMIN_ID).orElseGet(()->userRepository.save(new User(ADMIN_ID)));
        };
    }
}
