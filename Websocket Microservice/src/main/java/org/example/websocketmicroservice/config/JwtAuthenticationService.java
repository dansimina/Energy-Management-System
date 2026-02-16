package org.example.websocketmicroservice.config;

import org.example.websocketmicroservice.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JwtAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationService.class);

    @Value("${auth.service.host}")
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public UserPrincipal validateToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                authServiceUrl + "/validate",
                HttpMethod.GET,
                request,
                Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                HttpHeaders responseHeaders = response.getHeaders();
                String userId = responseHeaders.getFirst("X-User-Id");
                String username = responseHeaders.getFirst("X-Username");
                String role = responseHeaders.getFirst("X-User-Role");

                if (userId != null && username != null && role != null) {
                    log.info("User authenticated: {}", username);
                    return new UserPrincipal(userId, username, role);
                }
            }

            log.warn("Token validation failed");
            return null;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return null;
        }
    }
}