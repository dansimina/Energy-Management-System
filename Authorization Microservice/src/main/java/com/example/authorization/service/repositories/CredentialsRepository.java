package com.example.authorization.service.repositories;

import com.example.authorization.service.entities.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
    Optional<Credentials> findByUsername(String username);
}
