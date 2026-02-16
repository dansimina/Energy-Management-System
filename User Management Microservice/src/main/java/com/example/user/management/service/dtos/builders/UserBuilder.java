package com.example.user.management.service.dtos.builders;

import com.example.user.management.service.dtos.UserDTO;
import com.example.user.management.service.entities.User;

public class UserBuilder {

    private UserBuilder() {
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getAddress(), user.getAge());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User( userDTO.getId(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getAddress(),
                userDTO.getAge());
    }
}
