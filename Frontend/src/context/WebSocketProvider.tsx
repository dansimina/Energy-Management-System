import { useEffect, useState, useCallback, useRef } from 'react';
import { Client, type IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { ChatMessage } from './types/ChatMessage';
import type { UserStatusMessage } from './types/UserStatusMessage';
import type { ChatUser } from './types/ChatUser';
import type { AppNotification } from './types/AppNotification.ts';
import { WebSocketContext } from './useWebSocketContext';

export function WebSocketProvider({ children }: { children: React.ReactNode }) {
    const [connected, setConnected] = useState(false);
    const [notifications, setNotifications] = useState<AppNotification[]>([]);
    const [chatMessages, setChatMessages] = useState<ChatMessage[]>([]);
    const [onlineUsers, setOnlineUsers] = useState<ChatUser[]>([]);
    const [currentSessionId, setCurrentSessionId] = useState<string | null>(null);

    const clientRef = useRef<Client | null>(null);
    const sessionMapRef = useRef<Record<string, string>>({});
    const currentSessionIdRef = useRef<string | null>(null);

    useEffect(() => {
        currentSessionIdRef.current = currentSessionId;
    }, [currentSessionId]);

    const unreadCount = notifications.length;

    const getCurrentUser = useCallback(() => {
        try {
            const userStr = localStorage.getItem('user');
            return userStr && userStr !== 'null' ? JSON.parse(userStr) : null;
        } catch {
            return null;
        }
    }, []);

    const clearNotification = useCallback((id: string) => {
        setNotifications(prev => prev.filter(n => n.id !== id));
    }, []);

    const clearAllNotifications = useCallback(() => {
        setNotifications([]);
    }, []);

    const handleChatMessage = useCallback((message: ChatMessage) => {
        console.log('Chat message received:', message);
        const currentUser = getCurrentUser();

        if (message.senderType === 'SYSTEM' && message.content === 'SESSION_STARTED') {
            if (message.recipientId) {
                sessionMapRef.current[message.recipientId] = message.sessionId;
            }
            setCurrentSessionId(message.sessionId);
            return;
        }

        setChatMessages(prev => [...prev, message]);

        // Store session mapping for any message (in case we didn't get SESSION_STARTED)
        if (message.senderId && message.sessionId) {
            sessionMapRef.current[message.senderId] = message.sessionId;
        }

        const isFromOther = message.senderId !== currentUser?.id;
        const isActiveSession = message.sessionId === currentSessionIdRef.current;

        // Create notification for messages from other users or system alerts
        if (isFromOther && !isActiveSession) {
            if (message.senderType === 'SYSTEM' && message.content !== 'SESSION_STARTED') {
                setNotifications(prev => [{
                    id: `system-${message.messageId}`,
                    type: 'SYSTEM',
                    title: 'System Alert',
                    message: message.content.length > 60 ?
                        message.content.substring(0, 60) + '...' : message.content,
                    data: {
                        sessionId: message.sessionId,
                        senderId: message.senderId,
                        senderName: message.senderName
                    },
                    timestamp: message.timestamp
                }, ...prev]);
            }
            else if (message.senderType !== 'SYSTEM') {
                setNotifications(prev => [{
                    id: `chat-${message.messageId}`,
                    type: 'CHAT',
                    title: `New message from ${message.senderName}`,
                    message: message.content.length > 50 ?
                        message.content.substring(0, 50) + '...' : message.content,
                    data: {
                        sessionId: message.sessionId,
                        senderId: message.senderId,
                        senderName: message.senderName
                    },
                    timestamp: message.timestamp
                }, ...prev]);
            }
        }
    }, [getCurrentUser]);

    const handleUserStatus = useCallback((status: UserStatusMessage) => {
        console.log('User status update:', status);
        const currentUser = getCurrentUser();

        setOnlineUsers(prev => {
            if (status.user.id === currentUser?.id) return prev;

            if (status.status === 'ONLINE') {
                const exists = prev.findIndex(u => u.id === status.user.id);
                if (exists === -1) return [...prev, { ...status.user, isOnline: true }];
                const updated = [...prev];
                updated[exists] = { ...updated[exists], isOnline: true };
                return updated;
            }
            return prev.map(u => u.id === status.user.id ? { ...u, isOnline: false } : u);
        });
    }, [getCurrentUser]);

    const handleOnlineUsers = useCallback((users: ChatUser[]) => {
        console.log('Online users received:', users);
        setOnlineUsers(users.map(u => ({ ...u, isOnline: true })));
    }, []);

    const startChat = useCallback((recipientId: string) => {
        if (!clientRef.current?.connected) return;

        const existingSessionId = sessionMapRef.current[recipientId];
        if (existingSessionId) {
            setCurrentSessionId(existingSessionId);
            return;
        }

        clientRef.current.publish({
            destination: '/app/chat/start',
            body: JSON.stringify({ recipientId })
        });
    }, []);

    const sendMessage = useCallback((sessionId: string, content: string) => {
        if (!clientRef.current?.connected) return;

        clientRef.current.publish({
            destination: '/app/chat/send-message',
            body: JSON.stringify({ sessionId, content })
        });
    }, []);

    const getMessagesForSession = useCallback((sessionId: string) => {
        return chatMessages.filter(msg => msg.sessionId === sessionId);
    }, [chatMessages]);

    const getSessionIdForUser = useCallback((userId: string) => {
        return sessionMapRef.current[userId] || null;
    }, []);

    const disconnect = useCallback(() => {
        if (clientRef.current) {
            clientRef.current.deactivate();
            clientRef.current = null;
            setConnected(false);
        }
    }, []);

    const connect = useCallback(() => {
        const token = localStorage.getItem('token');
        const user = localStorage.getItem('user');

        if (!token || !user || user === 'null' || clientRef.current?.connected) return;

        if (clientRef.current) clientRef.current.deactivate();

        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost/api/websocket-service'),
            connectHeaders: { Authorization: `Bearer ${token}` },
            debug: (str) => console.log('STOMP:', str),
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,

            onConnect: () => {
                console.log('WebSocket Connected');
                setConnected(true);

                client.subscribe('/user/queue/chat', (msg: IMessage) => {
                    handleChatMessage(JSON.parse(msg.body));
                });

                client.subscribe('/topic/user-status', (msg: IMessage) => {
                    handleUserStatus(JSON.parse(msg.body));
                });

                client.subscribe('/user/queue/users', (msg: IMessage) => {
                    handleOnlineUsers(JSON.parse(msg.body));
                });

                client.subscribe('/user/queue/notifications', (msg: IMessage) => {
                    setNotifications(prev => [JSON.parse(msg.body), ...prev]);
                });

                client.subscribe('/topic/notifications', (msg: IMessage) => {
                    setNotifications(prev => [JSON.parse(msg.body), ...prev]);
                });
            },

            onDisconnect: () => {
                console.log('WebSocket Disconnected');
                setConnected(false);
            },

            onStompError: (frame) => {
                console.error('STOMP error:', frame.headers['message']);
                setConnected(false);
            },

            onWebSocketError: (event) => console.error('WebSocket error:', event),
            onWebSocketClose: () => setConnected(false)
        });

        clientRef.current = client;
        client.activate();
    }, [handleChatMessage, handleUserStatus, handleOnlineUsers]);

    const reconnect = useCallback(() => {
        disconnect();
        setChatMessages([]);
        setCurrentSessionId(null);
        sessionMapRef.current = {};
        setTimeout(connect, 100);
    }, [disconnect, connect]);

    useEffect(() => {
        connect();
        return () => disconnect();
    }, [connect, disconnect]);

    return (
        <WebSocketContext.Provider value={{
            connected, reconnect, disconnect,
            notifications, unreadCount, clearNotification, clearAllNotifications,
            chatMessages, onlineUsers, currentSessionId,
            startChat, sendMessage, setCurrentSessionId, getMessagesForSession, getSessionIdForUser
        }}>
            {children}
        </WebSocketContext.Provider>
    );
}