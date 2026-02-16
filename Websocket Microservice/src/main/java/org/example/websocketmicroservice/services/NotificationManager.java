package org.example.websocketmicroservice.services;

import org.example.websocketmicroservice.common.MessageType;
import org.example.websocketmicroservice.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationManager {
    @Value("${system.name}")
    private String SYSTEM_ID;

    private static final int MAX_QUEUED_MESSAGES = 100;

    private final SimpMessagingTemplate messaging;
    private final ChatSessionManager sessionManager;
    private final OnlineUserManager userManager;

    private final ConcurrentHashMap<String, Set<ChatMessage>> sessions = new ConcurrentHashMap<>();

    @Autowired
    public NotificationManager(ChatSessionManager sessionManager, OnlineUserManager userManager, SimpMessagingTemplate messaging) {
        this.sessionManager = sessionManager;
        this.userManager = userManager;
        this.messaging = messaging;
    }

    public void insertAlertMessage(UUID userId, UUID deviceId, Integer value, String timestamp) {
        String sessionId = sessionManager.createSession(userId.toString(), SYSTEM_ID);
        String content = String.format("Device %s has a consumption of %d W at %s", deviceId, value, timestamp);
        ChatMessage message = new ChatMessage(sessionId, MessageType.NOTIFICATION, SYSTEM_ID, "Monitoring System", content, userId.toString());

        if (userManager.isOnline(userId.toString())) {
            messaging.convertAndSendToUser(
                    userId.toString(),
                    "/queue/chat",
                    message
            );
        } else {
            sessions.compute(userId.toString(), (k, v) -> {
                Set<ChatMessage> messages = v != null ? v : ConcurrentHashMap.newKeySet();
                if (messages.size() < MAX_QUEUED_MESSAGES) {
                    messages.add(message);
                }
                return messages;
            });
        }
    }

    public void notifyUser(String userId) {
        if (!userManager.isOnline(userId)) {
            return;
        }

        Set<ChatMessage> messages = sessions.remove(userId);
        if (messages != null) {
            messages.forEach(message -> messaging.convertAndSendToUser(userId, "/queue/chat", message));
        }
    }
}