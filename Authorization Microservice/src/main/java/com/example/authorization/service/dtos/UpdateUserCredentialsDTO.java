package com.example.authorization.service.dtos;

import com.example.authorization.service.common.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class UpdateUserCredentialsDTO {
    private String username;

    private String password;

    @NotNull(message = "role is required")
    private UserRole role;

    public UpdateUserCredentialsDTO() {
    }

    public UpdateUserCredentialsDTO(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UpdateUserCredentialsDTO that = (UpdateUserCredentialsDTO) obj;
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, role);
    }

    @Override
    public String toString() {
        return "UpdateUserCredentialsDTO{" +
                "username='" + username + '\'' +
                ", password='" + (password != null && !password.isBlank() ? "[PROVIDED]" : "[NOT PROVIDED]") + '\'' +
                ", role=" + role +
                '}';
    }
}