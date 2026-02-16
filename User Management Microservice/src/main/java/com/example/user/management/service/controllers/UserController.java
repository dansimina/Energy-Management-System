package com.example.user.management.service.controllers;

import com.example.user.management.service.common.UserRole;
import com.example.user.management.service.common.security.RequireRole;
import com.example.user.management.service.dtos.UserDTO;
import com.example.user.management.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-service")
@Validated
@Tag(name = "User Management", description = "User profile and account management operations")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/me")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get current user profile - Requires JWT (User/Admin)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
        responseCode = "200",
        description = "Current user profile retrieved successfully. Uses X-User-Id header from JWT",
        content = @Content(schema = @Schema(implementation = UserDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> me(HttpServletRequest request) {
        UUID id = UUID.fromString(request.getHeader("X-User-Id"));
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/admin/get-users")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Get all users - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
        responseCode = "200",
        description = "List of all users retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

    @GetMapping("/user/get-user/{id}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get user by ID - Requires JWT (User/Admin)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
        responseCode = "200",
        description = "User profile retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PutMapping("/admin/update-user/{id}")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Update user - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
        responseCode = "200",
        description = "User updated successfully. Returns updated user",
        content = @Content(schema = @Schema(implementation = UserDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid user data")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated user details",
                required = true,
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            )
            @Valid @RequestBody UserDTO user) {
        UserDTO updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }
}