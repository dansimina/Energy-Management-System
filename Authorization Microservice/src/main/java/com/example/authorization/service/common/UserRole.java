package com.example.authorization.service.common;

public enum UserRole {
    ADMIN,
    USER;

    private UserRole() {}

    public static UserRole fromString(String value) {
        return valueOf(value.toUpperCase());
    }

    public String toString() {
        return name().toUpperCase();
    }
}
