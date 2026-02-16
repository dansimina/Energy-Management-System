package org.example.websocketmicroservice.services;

import org.example.websocketmicroservice.model.User;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserManager {
    private final Set<User> onlineUsers = ConcurrentHashMap.newKeySet();

    public boolean isOnline(String userId) {
        return onlineUsers.stream().anyMatch(user -> user.getId().equals(userId));
    }

    public void setOnline(User user) {
        onlineUsers.add(user);
    }

    public void setOffline(User user) {
        onlineUsers.remove(user);
    }

    public Set<User> getOnlineUsers() {
        return Set.copyOf(onlineUsers);
    }
}