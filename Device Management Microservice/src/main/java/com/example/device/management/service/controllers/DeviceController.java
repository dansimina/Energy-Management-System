package com.example.device.management.service.controllers;

import com.example.device.management.service.common.UserRole;
import com.example.device.management.service.common.security.RequireRole;
import com.example.device.management.service.dtos.DeviceDTO;
import com.example.device.management.service.dtos.UserDTO;
import com.example.device.management.service.services.DeviceService;
import com.example.device.management.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/device-service")
@Tag(name = "Device Management", description = "Device and user-device relationship management")
public class DeviceController {
    private final DeviceService deviceService;
    private final UserService userService;

    @Autowired
    public DeviceController(DeviceService deviceService, UserService userService) {
        this.deviceService = deviceService;
        this.userService = userService;
    }

    @GetMapping("/user/get-devices")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get all devices - Requires JWT (User/Admin)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
            responseCode = "200",
            description = "List of all devices retrieved successfully",
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.getDevices());
    }

    @GetMapping("/admin/get-unassigned-devices")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Get all unassigned devices - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
            responseCode = "200",
            description = "List of unassigned devices retrieved successfully",
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<List<DeviceDTO>> getUnassignedDevices() {
        return ResponseEntity.ok(deviceService.getUnassignedDevices());
    }

    @GetMapping("/user/get-device/{id}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get device by ID - Requires JWT (User/Admin)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
            responseCode = "200",
            description = "Device details retrieved successfully",
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<DeviceDTO> getDevice(
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.getDevice(id));
    }

    @GetMapping("/user/user-devices/{id}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get user's devices - Requires JWT (User/Admin)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
            responseCode = "200",
            description = "List of devices assigned to the user",
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<List<DeviceDTO>> getUserDevices(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserDevices(id));
    }

    @PostMapping("/admin/create-device")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Create device - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "201", description = "Device created successfully. Location header contains device URL")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<Void> createDevice(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Device details including name, type, and specifications",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))
            )
            @RequestBody DeviceDTO deviceDTO) {
        deviceService.insert(deviceDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(deviceDTO.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/admin/for-user/{userId}/add-device/{deviceId}")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Add device to user - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "204", description = "Device assigned to user successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User or device not found")
    public ResponseEntity<Void> addUserDevice(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID deviceId) {
        userService.insertUserDevice(userId, deviceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/update-device/{id}")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Update device - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
            responseCode = "200",
            description = "Device updated successfully. Returns updated device",
            content = @Content(schema = @Schema(implementation = DeviceDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<DeviceDTO> updateDevice(
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated device details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))
            )
            @RequestBody DeviceDTO deviceDTO) {
        DeviceDTO updated = deviceService.update(id, deviceDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/delete-device/{id}")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Delete device - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "204", description = "Device deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/for-user/{userId}/remove-device/{deviceId}")
    @RequireRole({UserRole.ADMIN})
    @Operation(summary = "Remove device from user - Requires JWT (Admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "204", description = "Device removed from user successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User or device not found")
    public ResponseEntity<Void> removeUserDevice(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID deviceId) {
        userService.deleteUserDevice(userId, deviceId);
        return ResponseEntity.noContent().build();
    }
}