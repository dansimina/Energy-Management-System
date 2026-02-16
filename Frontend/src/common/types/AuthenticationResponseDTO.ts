export interface AuthenticationResponseDTO {
    token: string;
    id: string;
    username: string;
    role: "USER" | "ADMIN";
}