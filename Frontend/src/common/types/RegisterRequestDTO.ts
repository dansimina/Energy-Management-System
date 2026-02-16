export interface RegisterRequestDTO {
    credentials: {
        username: string;
        password: string;
        role: "USER" | "ADMIN";
    };
    user: {
        firstName: string;
        lastName: string;
        email: string;
        address: string;
        age: number;
    };
}