export interface AppNotification {
    id: string;
    type: "CHAT" | "ALERT" | "INFO" | "WARNING" | "SYSTEM";
    title: string;
    message: string;
    data?: {
        sessionId?: string;
        senderId?: string;
        senderName?: string;
    };
    timestamp: string;
}