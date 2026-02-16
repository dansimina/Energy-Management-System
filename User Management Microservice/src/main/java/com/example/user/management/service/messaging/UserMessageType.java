package com.example.user.management.service.messaging;

import com.example.user.management.service.dtos.UserDTO;

import java.util.UUID;

public class UserMessageType {
    private OperationType type;
    private UUID id;
    private UserDTO user;

    public UserMessageType() {
    }

    public UserMessageType(OperationType type, UUID id, UserDTO user) {
        this.type = type;
        this.id = id;
        this.user = user;
    }

    public OperationType getType() {
        return type;
    }
    public void setType(OperationType type) {
        this.type = type;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UserDTO getUser() {
        return user;
    }
    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserMessageType{" +
                "type=" + type +
                ", id=" + id +
                ", user=" + user +
                '}';
    }
}
