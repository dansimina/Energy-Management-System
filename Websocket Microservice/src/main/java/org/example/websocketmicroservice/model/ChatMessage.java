package org.example.websocketmicroservice.model;

import org.example.websocketmicroservice.common.MessageType;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ChatMessage {
    private String messageId;
    private String sessionId;
    private MessageType senderType;
    private String senderId;
    private String senderName;
    private String content;
    private String recipientId;
    private LocalDateTime timestamp;

    public ChatMessage() {}

    public ChatMessage(String sessionId, MessageType senderType, String senderId,
                      String senderName, String content, String recipientId) {
        this.messageId = UUID.randomUUID().toString();
        this.sessionId = sessionId;
        this.senderType = senderType;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.recipientId = recipientId;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public MessageType getSenderType() { return senderType; }
    public void setSenderType(MessageType senderType) { this.senderType = senderType; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChatMessage other = (ChatMessage) obj;
        return messageId.equals(other.messageId);
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "messageId='" + messageId + '\'' + ", sessionId='" + sessionId + '\'' + ", senderType=" + senderType + ", senderId='" + senderId + '\'' + ", senderName='" + senderName + '\'' + ", content='" + content + '\'' + ", recipientId='" + recipientId + '\'' + ", timestamp=" + timestamp + '}';
    }
}