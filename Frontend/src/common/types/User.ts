export interface User {
    id: string | null;
    username: string,
    role: "USER" | "ADMIN";
    firstName: string;
    lastName: string;
    email: string;
    address: string;
    age: number;
}