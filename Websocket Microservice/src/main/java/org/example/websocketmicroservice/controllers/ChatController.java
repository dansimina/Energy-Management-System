package org.example.websocketmicroservice.controllers;

import org.example.websocketmicroservice.common.MessageType;
import org.example.websocketmicroservice.model.ChatMessage;
import org.example.websocketmicroservice.security.UserPrincipal;
import org.example.websocketmicroservice.services.ChatSessionManager;
import org.example.websocketmicroservice.services.ChatbotService;
import org.example.websocketmicroservice.services.OnlineUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messaging;
    private final ChatSessionManager sessionManager;
    private final OnlineUserManager userManager;
    private final ChatbotService chatbotService;

    @Value("${chatbot.name}")
    private String CHATBOT_ID;

    @Value("${system.name}")
    private String SYSTEM_ID;

    public ChatController(SimpMessagingTemplate messaging,
                          ChatSessionManager sessionManager,
                          OnlineUserManager userManager,
                          ChatbotService chatbotService) {
        this.messaging = messaging;
        this.sessionManager = sessionManager;
        this.userManager = userManager;
        this.chatbotService = chatbotService;
    }

    @MessageMapping("/chat/start")
    public void startChat(@Payload ChatMessage msg, StompHeaderAccessor accessor) {
        LOGGER.info("=== START CHAT REQUEST ===");
        LOGGER.info("Message received: {}", msg);

        UserPrincipal user = (UserPrincipal) accessor.getUser();
        LOGGER.info("User: {}", user);
        LOGGER.info("RecipientId: {}", msg.getRecipientId());

        if (user == null || msg.getRecipientId() == null) {
            LOGGER.warn("Invalid request - user: {}, recipientId: {}", user, msg.getRecipientId());
            return;
        }

        String sessionId = sessionManager.createSession(user.id(), msg.getRecipientId());
        LOGGER.info("Session created: {}", sessionId);

        ChatMessage systemMsg = new ChatMessage(sessionId, MessageType.SYSTEM, SYSTEM_ID,
                "System", "SESSION_STARTED", user.id());

        LOGGER.info("Sending system message to user: {}", user.id());
        messaging.convertAndSendToUser(user.id(), "/queue/chat", systemMsg);
        LOGGER.info("System message sent");

        if (CHATBOT_ID.equals(msg.getRecipientId())) {
            LOGGER.info("Starting chatbot conversation");
            ChatMessage greeting = new ChatMessage(sessionId, MessageType.CHATBOT, CHATBOT_ID,
                    "Assistant", "Hello!  How can I help you today?", user.id());
            messaging.convertAndSendToUser(user.id(), "/queue/chat", greeting);
            LOGGER.info("Chatbot greeting sent");
        }
        LOGGER.info("=== START CHAT COMPLETE ===");
    }

    @MessageMapping("/chat/send-message")
    public void sendMessage(@Payload ChatMessage msg, StompHeaderAccessor accessor) {
        UserPrincipal user = (UserPrincipal) accessor.getUser();
        if (user == null || !sessionManager.exists(msg.getSessionId())) return;

        String recipientId = sessionManager.getOtherUser(msg.getSessionId(), user.id());
        if (recipientId == null) return;

        MessageType senderType = user.isAdmin() ? MessageType.ADMIN : MessageType.USER;
        ChatMessage outgoing = new ChatMessage(msg.getSessionId(), senderType, user.id(),
                user.username(), msg.getContent(), recipientId);

        messaging.convertAndSendToUser(user.id(), "/queue/chat", outgoing);

        if (CHATBOT_ID.equals(recipientId)) {
            // Async approach - use subscribe with callbacks
            chatbotService.processMessage(msg.getContent())
                    .subscribe(
                            response -> {
                                // Success callback - send the bot's response
                                ChatMessage botReply = new ChatMessage(
                                        msg.getSessionId(),
                                        MessageType.CHATBOT,
                                        CHATBOT_ID,
                                        "Assistant",
                                        response,
                                        user.id()
                                );
                                messaging.convertAndSendToUser(user.id(), "/queue/chat", botReply);
                                LOGGER.info("Chatbot response sent successfully");
                            },
                            error -> {
                                // Error callback - send error message to user
                                LOGGER.error("Error processing chatbot message: {}", error.getMessage());
                                ChatMessage errorMsg = new ChatMessage(
                                        msg.getSessionId(),
                                        MessageType.CHATBOT,
                                        CHATBOT_ID,
                                        "Assistant",
                                        "Sorry, I encountered an error. Please try again.",
                                        user.id()
                                );
                                messaging.convertAndSendToUser(user.id(), "/queue/chat", errorMsg);
                            }
                    );
        } else if (userManager.isOnline(recipientId)) {
            messaging.convertAndSendToUser(recipientId, "/queue/chat", outgoing);
        }
    }
}