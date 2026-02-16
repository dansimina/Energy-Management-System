package org.example.websocketmicroservice.controllers;

import org.example.websocketmicroservice.common.UserStatus;
import org.example.websocketmicroservice.model.User;
import org.example.websocketmicroservice.model.UserStatusMessage;
import org.example.websocketmicroservice.security.UserPrincipal;
import org.example.websocketmicroservice.services.NotificationManager;
import org.example.websocketmicroservice.services.OnlineUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;
import java.util.List;

@Controller
public class OnlineUsersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineUsersController.class);

    private final OnlineUserManager userManager;
    private final SimpMessagingTemplate messaging;
    private final TaskScheduler taskScheduler;
    private final NotificationManager notificationManager;

    @Autowired
    public OnlineUsersController(OnlineUserManager userManager, SimpMessagingTemplate messaging, @Qualifier("taskScheduler") TaskScheduler taskScheduler, NotificationManager notificationManager) {
        this.userManager = userManager;
        this.messaging = messaging;
        this.taskScheduler = taskScheduler;
        this.notificationManager = notificationManager;
    }

    @MessageMapping("/users/online")
    public void getOnlineUsers(StompHeaderAccessor accessor) {
        UserPrincipal user = (UserPrincipal) accessor.getUser();
        if (user == null) {
            LOGGER.warn("User not authenticated");
            return;
        }

        messaging.convertAndSendToUser(user.id(), "/queue/users",
            userManager.getOnlineUsers().stream()
                .filter(u -> !u.getId().equals(user.id()))
                .toList());
    }

    @EventListener
    public void onConnect(SessionConnectedEvent event) {
        if (!(event.getUser() instanceof UserPrincipal user)) return;

        User connectedUser = new User(user.id(), user.username(), user.role());
        userManager.setOnline(connectedUser);
        LOGGER.info("User connected: {}", user.username());

        // Broadcast status change to all users
        UserStatusMessage status = new UserStatusMessage(UserStatus.ONLINE, connectedUser);
        messaging.convertAndSend("/topic/user-status", status);

        // Schedule sending the online users list to the newly connected user
        // This delay ensures the client's subscriptions are ready
        taskScheduler.schedule(() -> {
            List<User> otherUsers = userManager.getOnlineUsers().stream()
                    .filter(u -> !u.getId().equals(user.id()))
                    .toList();
            LOGGER.info("Sending online users list to {}: {}", user.username(), otherUsers);
            messaging.convertAndSendToUser(user.id(), "/queue/users", otherUsers);
            notificationManager.notifyUser(user.id());
        }, Instant.now().plusMillis(500));
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        if (!(event.getUser() instanceof UserPrincipal user)) return;

        User disconnectedUser = new User(user.id(), user.username(), user.role());
        userManager.setOffline(disconnectedUser);
        LOGGER.info("User disconnected: {}", user.username());

        UserStatusMessage status = new UserStatusMessage(UserStatus.OFFLINE, disconnectedUser);
        messaging.convertAndSend("/topic/user-status", status);
    }
}