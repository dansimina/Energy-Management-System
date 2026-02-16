package org.example.websocketmicroservice.services;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatSessionManager {
    private final ConcurrentHashMap<String, Set<String>> sessions = new ConcurrentHashMap<>();

    public String createSession(String userId1, String userId2) {
        String sessionId = userId1.compareTo(userId2) < 0
            ? "session-" + userId1 + "-" + userId2
            : "session-" + userId2 + "-" + userId1;

        sessions.putIfAbsent(sessionId, ConcurrentHashMap.newKeySet());
        sessions.get(sessionId).add(userId1);
        sessions.get(sessionId).add(userId2);

        return sessionId;
    }

    public String getOtherUser(String sessionId, String currentUserId) {
        Set<String> users = sessions.get(sessionId);
        return users == null ? null : users.stream()
            .filter(id -> !id.equals(currentUserId))
            .findFirst()
            .orElse(null);
    }

    public boolean exists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}