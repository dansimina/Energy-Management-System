export interface UserStatusMessage {
    status: "ONLINE" | "OFFLINE";
    user: {
        id: string;
        username: string;
        role: string;
    };
}