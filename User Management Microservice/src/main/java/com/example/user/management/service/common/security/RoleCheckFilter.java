package com.example.user.management.service.common.security;

import com.example.user.management.service.common.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Arrays;

@Component
public class RoleCheckFilter implements Filter {

    private final RequestMappingHandlerMapping handlerMapping;

    public RoleCheckFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Get the handler for this request
            HandlerMethod handlerMethod = (HandlerMethod) handlerMapping.getHandler(httpRequest).getHandler();

            // Check if method has @RequireRole annotation
            RequireRole requireRole = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireRole.class);

            if (requireRole == null) {
                chain.doFilter(request, response);
                return;
            }

            // Get role from header
            String roleHeader = httpRequest.getHeader("X-User-Role");

            if (roleHeader == null || roleHeader.trim().isEmpty()) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing role header");
                return;
            }

            // Parse role
            UserRole userRole;
            try {
                userRole = UserRole.fromString(roleHeader);
            } catch (Exception e) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid role");
                return;
            }

            // Check if user has required role
            boolean hasPermission = Arrays.asList(requireRole.value()).contains(userRole);

            if (!hasPermission) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
                return;
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            chain.doFilter(request, response);
        }
    }
}