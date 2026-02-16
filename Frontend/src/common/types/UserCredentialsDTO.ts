export interface UserCredentialsDTO {
    username: string;
    password?: string;
    role: "ADMIN" | "USER";
}