package com.example.device.management.service.repositories;

import com.example.device.management.service.entities.Device;
import com.example.device.management.service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
