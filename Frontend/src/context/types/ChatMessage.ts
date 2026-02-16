export interface ChatMessage {
    messageId: string;
    sessionId: string;
    senderType: "USER" | "ADMIN" | "CHATBOT" | "SYSTEM";
    senderId: string;
    senderName: string;
    content: string;
    recipientId: string;
    timestamp: string;
}