import { useState, useEffect, useRef } from "react";
import { useSearchParams } from "react-router-dom";
import {
    Box, Container, Paper, List, ListItem, ListItemButton, ListItemText,
    ListItemAvatar, Avatar, TextField, IconButton, Typography, Divider,
    Badge, CircularProgress, Chip
} from "@mui/material";
import SendIcon from "@mui/icons-material/Send";
import SmartToyIcon from "@mui/icons-material/SmartToy";
import NotificationsIcon from "@mui/icons-material/Notifications";
import AppNavigationBar from "../common/components/AppNavigationBar";
import type { User } from "../common/types/User";
import type { ChatUser } from "../context/types/ChatUser";
import { useWebSocket } from "../context/useWebSocketContext";

function ChatPage() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [currentUser, setCurrentUser] = useState<User | null>(null);
    const [selectedUser, setSelectedUser] = useState<ChatUser | null>(null);
    const [messageInput, setMessageInput] = useState("");
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const {
        connected,
        onlineUsers,
        currentSessionId,
        startChat,
        sendMessage,
        getMessagesForSession,
        setCurrentSessionId,
        getSessionIdForUser,
        chatMessages
    } = useWebSocket();

    const currentMessages = currentSessionId ? getMessagesForSession(currentSessionId) : [];

    const systemUser: ChatUser = {
        id: "SYSTEM",
        username: "System Notifications",
        role: "SYSTEM",
        isOnline: true
    };

    const chatbotUser: ChatUser = {
        id: "CHATBOT",
        username: "Assistant",
        role: "CHATBOT",
        isOnline: true
    };

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser && storedUser !== "null") {
            try {
                setCurrentUser(JSON.parse(storedUser));
            } catch (e) {
                console.error("Invalid user in localStorage:", e);
            }
        }
    }, []);

    // Handle session from URL parameter
    useEffect(() => {
        const sessionId = searchParams.get("session");
        if (sessionId && connected && currentUser) {
            const messageFromSession = chatMessages.find(m => m.sessionId === sessionId);
            if (messageFromSession) {
                const senderId = messageFromSession.senderId !== currentUser.id
                    ? messageFromSession.senderId
                    : messageFromSession.recipientId;
                const senderName = messageFromSession.senderId !== currentUser.id
                    ? messageFromSession.senderName
                    : "User";

                if (senderId === "SYSTEM") {
                    setSelectedUser(systemUser);
                } else if (senderId === "CHATBOT") {
                    setSelectedUser(chatbotUser);
                } else {
                    const user = onlineUsers.find(u => u.id === senderId);
                    setSelectedUser(user || {
                        id: senderId || "",
                        username: senderName || "User",
                        role: "USER",
                        isOnline: false
                    });
                }
                setCurrentSessionId(sessionId);
            }
            // Clear the URL parameter
            setSearchParams({});
        }
    }, [searchParams, connected, chatMessages, currentUser, onlineUsers, setCurrentSessionId, setSearchParams]);

    const handleUserSelect = (user: ChatUser) => {
        console.log("Selected:", user.username);
        setSelectedUser(user);
        setMessageInput("");

        const existingSessionId = getSessionIdForUser(user.id);
        if (existingSessionId) {
            setCurrentSessionId(existingSessionId);
        } else {
            setCurrentSessionId(null);
            startChat(user.id);
        }
    };

    const handleSendMessage = () => {
        if (!messageInput.trim() || !currentSessionId) return;
        sendMessage(currentSessionId, messageInput.trim());
        setMessageInput("");
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleSendMessage();
        }
    };

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [currentMessages]);

    if (!currentUser) {
        return (
            <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
                <AppNavigationBar />
                <Container sx={{ flexGrow: 1, py: 4 }}>
                    <Typography variant="h5" color="error">
                        Please log in to access chat
                    </Typography>
                </Container>
            </Box>
        );
    }

    return (
        <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
            <AppNavigationBar />

            <Container component="main" sx={{ flexGrow: 1, py: 4 }} maxWidth="xl">
                {!connected && (
                    <Box sx={{ mb: 2, display: "flex", alignItems: "center", gap: 2 }}>
                        <CircularProgress size={20} />
                        <Typography>Connecting to chat...</Typography>
                    </Box>
                )}

                <Box sx={{ display: "flex", height: "calc(100vh - 150px)", gap: 2 }}>
                    <Paper sx={{ width: 300, display: "flex", flexDirection: "column" }}>
                        <Box sx={{ p: 2, borderBottom: 1, borderColor: "divider" }}>
                            <Typography variant="h6">Chats</Typography>
                        </Box>

                        <List sx={{ flexGrow: 1, overflow: "auto" }}>
                            {/* System Chat */}
                            <ListItem disablePadding>
                                <ListItemButton
                                    selected={selectedUser?.id === systemUser.id}
                                    onClick={() => handleUserSelect(systemUser)}
                                >
                                    <ListItemAvatar>
                                        <Avatar sx={{ bgcolor: "orange" }}>
                                            <NotificationsIcon />
                                        </Avatar>
                                    </ListItemAvatar>
                                    <ListItemText primary="System" secondary="Notifications" />
                                    <Chip label="SYSTEM" size="small" color="warning" />
                                </ListItemButton>
                            </ListItem>

                            <Divider />

                            {/* Chatbot */}
                            <ListItem disablePadding>
                                <ListItemButton
                                    selected={selectedUser?.id === chatbotUser.id}
                                    onClick={() => handleUserSelect(chatbotUser)}
                                >
                                    <ListItemAvatar>
                                        <Avatar sx={{ bgcolor: "green" }}>
                                            <SmartToyIcon />
                                        </Avatar>
                                    </ListItemAvatar>
                                    <ListItemText primary="Assistant" secondary="AI Chatbot" />
                                    <Chip label="BOT" size="small" color="success" />
                                </ListItemButton>
                            </ListItem>

                            <Divider />

                            {onlineUsers.length === 0 && connected && (
                                <Box sx={{ p: 2, textAlign: "center" }}>
                                    <Typography variant="body2" color="text.secondary">
                                        No other users online
                                    </Typography>
                                </Box>
                            )}

                            {onlineUsers.map(user => (
                                <ListItem key={user.id} disablePadding>
                                    <ListItemButton
                                        selected={selectedUser?.id === user.id}
                                        onClick={() => handleUserSelect(user)}
                                    >
                                        <ListItemAvatar>
                                            <Badge color="success" variant="dot" invisible={!user.isOnline}>
                                                <Avatar sx={{ bgcolor: user.role === "ADMIN" ? "red" : "purple" }}>
                                                    {user.username[0]?.toUpperCase()}
                                                </Avatar>
                                            </Badge>
                                        </ListItemAvatar>
                                        <ListItemText
                                            primary={user.username}
                                            secondary={user.isOnline ? "Online" : "Offline"}
                                        />
                                        {user.role === "ADMIN" && (
                                            <Chip label="ADMIN" size="small" color="error" />
                                        )}
                                    </ListItemButton>
                                </ListItem>
                            ))}
                        </List>
                    </Paper>

                    <Paper sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
                        {selectedUser ? (
                            <>
                                <Box sx={{
                                    p: 2,
                                    borderBottom: 1,
                                    borderColor: "divider",
                                    display: "flex",
                                    alignItems: "center",
                                    gap: 2
                                }}>
                                    <Avatar sx={{
                                        bgcolor: selectedUser.id === "SYSTEM" ? "orange"
                                            : selectedUser.id === "CHATBOT" ? "green"
                                            : selectedUser.role === "ADMIN" ? "red" : "purple"
                                    }}>
                                        {selectedUser.id === "SYSTEM" ? (
                                            <NotificationsIcon />
                                        ) : selectedUser.id === "CHATBOT" ? (
                                            <SmartToyIcon />
                                        ) : (
                                            selectedUser.username[0]?.toUpperCase()
                                        )}
                                    </Avatar>
                                    <Box>
                                        <Typography variant="h6">{selectedUser.username}</Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            {selectedUser.id === "SYSTEM" ? "System Notifications"
                                                : selectedUser.id === "CHATBOT" ? "AI Assistant"
                                                : selectedUser.isOnline ? "Online" : "Offline"}
                                        </Typography>
                                    </Box>
                                </Box>

                                <Box sx={{
                                    flexGrow: 1,
                                    overflow: "auto",
                                    p: 2,
                                    display: "flex",
                                    flexDirection: "column",
                                    gap: 1
                                }}>
                                    {!currentSessionId && (
                                        <Box sx={{ textAlign: "center", py: 4 }}>
                                            <CircularProgress size={24} />
                                            <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                                                Establishing connection...
                                            </Typography>
                                        </Box>
                                    )}

                                    {currentMessages.length === 0 && currentSessionId && (
                                        <Box sx={{ textAlign: "center", py: 4 }}>
                                            <Typography variant="body2" color="text.secondary">
                                                {selectedUser.id === "SYSTEM"
                                                    ? "No notifications yet"
                                                    : "Start the conversation"}
                                            </Typography>
                                        </Box>
                                    )}

                                    {currentMessages.map(message => {
                                        const isOwnMessage = message.senderId === currentUser.id;
                                        const isSystemMessage = message.senderType === "SYSTEM" || message.senderId === "SYSTEM";

                                        return (
                                            <Box key={message.messageId} sx={{
                                                display: "flex",
                                                justifyContent: isSystemMessage ? "center" : (isOwnMessage ? "flex-end" : "flex-start")
                                            }}>
                                                <Paper sx={{
                                                    p: 1.5,
                                                    maxWidth: isSystemMessage ? "90%" : "70%",
                                                    bgcolor: isSystemMessage ? "warning.light"
                                                        : isOwnMessage ? "primary.main" : "grey.200",
                                                    color: isSystemMessage ? "warning.contrastText"
                                                        : isOwnMessage ? "white" : "text.primary"
                                                }}>
                                                    {!isOwnMessage && !isSystemMessage && (
                                                        <Typography variant="caption" sx={{
                                                            fontWeight: "bold",
                                                            display: "block",
                                                            mb: 0.5
                                                        }}>
                                                            {message.senderName}
                                                        </Typography>
                                                    )}
                                                    <Typography variant="body1">{message.content}</Typography>
                                                    <Typography variant="caption"
                                                        sx={{ display: "block", mt: 0.5, opacity: 0.7 }}>
                                                        {new Date(message.timestamp).toLocaleTimeString()}
                                                    </Typography>
                                                </Paper>
                                            </Box>
                                        );
                                    })}
                                    <div ref={messagesEndRef} />
                                </Box>

                                <Box sx={{ p: 2, borderTop: 1, borderColor: "divider", display: "flex", gap: 1 }}>
                                    <TextField
                                        fullWidth
                                        placeholder={
                                            selectedUser.id === "SYSTEM"
                                                ? "System notifications are read-only"
                                                : currentSessionId ? "Type a message..." : "Connecting..."
                                        }
                                        value={messageInput}
                                        onChange={e => setMessageInput(e.target.value)}
                                        onKeyDown={handleKeyDown}
                                        disabled={!currentSessionId || selectedUser.id === "SYSTEM"}
                                    />
                                    <IconButton
                                        color="primary"
                                        onClick={handleSendMessage}
                                        disabled={!messageInput.trim() || !currentSessionId || selectedUser.id === "SYSTEM"}
                                    >
                                        <SendIcon />
                                    </IconButton>
                                </Box>
                            </>
                        ) : (
                            <Box sx={{ flexGrow: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
                                <Typography variant="h6" color="text.secondary">
                                    Select a chat to start messaging
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                </Box>
            </Container>
        </Box>
    );
}

export default ChatPage;