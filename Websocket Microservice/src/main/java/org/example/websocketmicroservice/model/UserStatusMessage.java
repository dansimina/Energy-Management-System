package org.example.websocketmicroservice.model;

import org.example.websocketmicroservice.common.UserStatus;

public record UserStatusMessage(UserStatus status, User user) {}