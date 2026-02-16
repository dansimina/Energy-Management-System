import type { ChatMessage } from './ChatMessage';
import type { ChatUser } from './ChatUser';
import type { AppNotification } from './AppNotification';

export interface WebSocketContextType {
    connected: boolean;
    reconnect: () => void;
    disconnect: () => void;
    notifications: AppNotification[];
    unreadCount: number;
    clearNotification: (id: string) => void;
    clearAllNotifications: () => void;
    chatMessages: ChatMessage[];
    onlineUsers: ChatUser[];
    currentSessionId: string | null;
    startChat: (recipientId: string) => void;
    sendMessage: (sessionId: string, content: string) => void;
    setCurrentSessionId: (sessionId: string | null) => void;
    getMessagesForSession: (sessionId: string) => ChatMessage[];
    getSessionIdForUser: (userId: string) => string | null;
}