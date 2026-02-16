package com.example.authorization.service.dtos;

import java.util.Objects;

public class RegisterRequestDTO {
    private UserCredentialsDTO credentials;
    private UserDTO user;

    public RegisterRequestDTO() {
    }

    public RegisterRequestDTO(UserCredentialsDTO credentials, UserDTO user) {
        this.credentials = credentials;
        this.user = user;
    }

    public UserCredentialsDTO getCredentials() {
        return credentials;
    }

    public void setCredentials(UserCredentialsDTO credentials) {
        this.credentials = credentials;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object O) {
        if (O == this) return true;
        if (!(O instanceof RegisterRequestDTO request)) {
            return false;
        }
        return request.credentials.equals(this.credentials) && request.user.equals(this.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentials, user);
    }

    @Override
    public String toString() {
        return "RegisterRequestDTO{" +
                "credentials=" + credentials +
                ", user=" + user +
                '}';
    }
}
