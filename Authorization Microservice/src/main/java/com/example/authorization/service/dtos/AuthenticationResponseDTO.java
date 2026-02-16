package com.example.authorization.service.dtos;


import java.util.Objects;

public class AuthenticationResponseDTO {
    private String token;
    private String id;
    private String username;
    private String role;

    public AuthenticationResponseDTO() {
    }

    public AuthenticationResponseDTO(String token, String id, String username, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AuthenticationResponseDTO that = (AuthenticationResponseDTO) obj;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "AuthenticationResponseDTO{" +
                "token='" + token + '\'' +
                '}';
    }
}
