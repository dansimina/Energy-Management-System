package org.example.websocketmicroservice.security;

import org.example.websocketmicroservice.common.UserRole;
import java.security.Principal;

public record UserPrincipal(String id, String username, String role) implements Principal {

    @Override
    public String getName() {
        return id;
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.toString().equals(role);
    }
}