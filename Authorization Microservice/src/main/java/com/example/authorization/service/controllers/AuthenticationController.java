package com.example.authorization.service.controllers;

import com.example.authorization.service.config.JwtService;
import com.example.authorization.service.dtos.*;
import com.example.authorization.service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.authorization.service.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/auth-service")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                   JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login - No JWT required")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully authenticated. Returns JWT token and user info. Also sets X-User-Id, X-User-Username, X-User-Role headers",
        content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthenticationResponseDTO> authentication(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Username and password for authentication",
                required = true,
                content = @Content(schema = @Schema(implementation = AuthenticationRequestDTO.class))
            )
            @RequestBody AuthenticationRequestDTO authRequest,
            HttpServletResponse response) {
        AuthenticationResponseDTO token;

        try {
            token = authenticationService.authenticate(authRequest);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        response.setHeader("X-User-Id", token.getId());
        response.setHeader("X-User-Username", token.getUsername());
        response.setHeader("X-User-Role", token.getRole());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user - No JWT required")
    @ApiResponse(
        responseCode = "200",
        description = "Successfully registered. Returns JWT token and user info. Also sets X-User-Id, X-User-Username, X-User-Role headers",
        content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Registration failed")
    public ResponseEntity<AuthenticationResponseDTO> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration details including username, password, email, and profile information",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequestDTO.class))
            )
            @RequestBody RegisterRequestDTO registerRequest,
            HttpServletResponse response) {
        AuthenticationResponseDTO token;

        try {
            token = authenticationService.insert(registerRequest, true);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        response.setHeader("X-User-Id", token.getId());
        response.setHeader("X-User-Username", token.getUsername());
        response.setHeader("X-User-Role", token.getRole());

        return ResponseEntity.ok(token);
    }

    @GetMapping("/admin/user/{id}")
    @Operation(summary = "Get user credentials - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(
        responseCode = "200",
        description = "User credentials retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserCredentialsDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserCredentialsDTO> getUser(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(authenticationService.findUserCredentialById(id));
    }

    @PostMapping("/admin/create-user")
    @Operation(summary = "Create user (Admin) - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "201", description = "User created successfully. Location header contains user URL")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    public ResponseEntity<Void> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration details for admin-created user",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequestDTO.class))
            )
            @RequestBody RegisterRequestDTO registerRequestDTO) {
        AuthenticationResponseDTO response = authenticationService.insert(registerRequestDTO, false);

        String userId = jwtService.extractUserId(response.getToken()).toString();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/admin/update-user-credentials/{id}")
    @Operation(summary = "Update user credentials - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "204", description = "User credentials updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> update(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated user credentials including username, password, and role",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateUserCredentialsDTO.class))
            )
            @RequestBody UpdateUserCredentialsDTO credentials) {
        authenticationService.update(id, credentials);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/delete-user/{id}")
    @Operation(summary = "Delete user - Requires JWT (Admin only)",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID (UUID format)", required = true)
            @PathVariable UUID id) {
        authenticationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token - Requires JWT",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponse(responseCode = "200", description = "Token is valid. Returns X-User-Id, X-Username, X-User-Role headers")
    @ApiResponse(responseCode = "401", description = "Token is invalid, expired, or missing")
    public ResponseEntity<Void> validateToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            response.setHeader("X-User-Id", jwtService.extractUserId(token).toString());
            response.setHeader("X-Username", username);
            response.setHeader("X-User-Role", jwtService.extractRole(token));

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}