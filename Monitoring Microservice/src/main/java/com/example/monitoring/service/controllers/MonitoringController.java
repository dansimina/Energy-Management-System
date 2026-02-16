package com.example.monitoring.service.controllers;

import com.example.monitoring.service.common.UserRole;
import com.example.monitoring.service.common.security.RequireRole;
import com.example.monitoring.service.dtos.DeviceDataDTO;
import com.example.monitoring.service.dtos.HourlyConsumptionDTO;
import com.example.monitoring.service.services.DeviceDataService;
import com.example.monitoring.service.services.DeviceService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/monitoring-service")
@Tag(name = "Monitoring", description = "Device monitoring and consumption data operations")
public class MonitoringController {
    private final DeviceService deviceService;
    private final DeviceDataService deviceDataService;

    @Autowired
    public MonitoringController(DeviceService deviceService, DeviceDataService deviceDataService) {
        this.deviceService = deviceService;
        this.deviceDataService = deviceDataService;
    }

    @GetMapping("/user/device-data/{id}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get all device data - Requires JWT (User or Admin)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Device data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - User or Admin access required")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<List<DeviceDataDTO>> getDeviceData(
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID id) {
        try {
            List<DeviceDataDTO> deviceData = deviceDataService.findByDevice(id);
            return ResponseEntity.ok(deviceData);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/device-data/{id}/from-date/{date}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get device data from specific date - Requires JWT (User or Admin)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Device data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - User or Admin access required")
    @ApiResponse(responseCode = "404", description = "Device not found")
    public ResponseEntity<List<DeviceDataDTO>> getDeviceDataByDate(
            @Parameter(description = "Device ID (UUID format)", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Date (YYYY-MM-DD)", required = true)
            @PathVariable LocalDate date) {
        try {
            List<DeviceDataDTO> deviceData = deviceDataService.findByDeviceAndDate(id, date);
            return ResponseEntity.ok(deviceData);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user/hourly-consumption/{date}")
    @RequireRole({UserRole.USER, UserRole.ADMIN})
    @Operation(summary = "Get hourly consumption for devices - Requires JWT (User or Admin)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Hourly consumption data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - User or Admin access required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<HourlyConsumptionDTO>> getUserHourlyConsumption(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "List of device IDs",
                required = true,
                content = @Content(schema = @Schema(implementation = List.class))
            )
            @RequestBody List<UUID> deviceIds,
            @Parameter(description = "Date (YYYY-MM-DD)", required = true)
            @PathVariable LocalDate date) {
        try {
            List<HourlyConsumptionDTO> hourlyData =
                    deviceDataService.getHourlyConsumptionForDevices(deviceIds, date);
            return ResponseEntity.ok(hourlyData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}