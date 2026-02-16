package com.example.device.management.service.dtos.builders;

import com.example.device.management.service.dtos.UserDTO;
import com.example.device.management.service.entities.User;

public class UserBuilder {
    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(userDTO.getId());
    }
}
