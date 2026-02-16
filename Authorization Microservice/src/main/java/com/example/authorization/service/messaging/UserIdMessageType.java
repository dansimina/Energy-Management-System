package com.example.authorization.service.messaging;


import java.util.UUID;

public class UserIdMessageType {
    private OperationType type;
    private UUID id;

    public UserIdMessageType() {
    }

    public UserIdMessageType(OperationType type, UUID id) {
        this.type = type;
        this.id = id;
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

    @Override
    public String toString() {
        return "UserIdMessageType{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }
}
