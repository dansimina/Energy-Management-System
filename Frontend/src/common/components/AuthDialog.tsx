import {useState} from "react";
import {
    Alert,
    Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    CircularProgress,
    Tabs,
    Tab,
} from "@mui/material";

import {login, register, getUser} from "../api/auth-api.ts";
import type {User} from "../types/User.ts";
import type {AuthenticationRequestDTO} from "../types/AuthenticationRequestDTO.ts";
import type {RegisterRequestDTO} from "../types/RegisterRequestDTO.ts";

interface AuthDialogProps {
    open: boolean;
    close: () => void;
    success: () => void;
}

function AuthDialog({open, close, success}: AuthDialogProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [age, setAge] = useState(0);
    const [role, setRole] = useState<"USER" | "ADMIN">("USER");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isRegister, setIsRegister] = useState(false);

    const handleSubmitLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request: AuthenticationRequestDTO = {
                username,
                password,
            };

            const loginResponse = await login(request);
            localStorage.setItem("token", loginResponse.data.token);

            await loadUserDetails(
                loginResponse.data.id,
                loginResponse.data.username,
                loginResponse.data.role,
            );

            success();
            handleClose();
        } catch (error) {
            setError("Invalid username or password");
            console.error("Login error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSubmitRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request: RegisterRequestDTO = {
                credentials: {
                    username,
                    password,
                    role,
                },
                user: {
                    firstName,
                    lastName,
                    email,
                    address,
                    age,
                },
            };

            const registerResponse = await register(request);
            localStorage.setItem("token", registerResponse.data.token);

            await loadUserDetails(
                registerResponse.data.id,
                registerResponse.data.username,
                registerResponse.data.role,
            );

            success();
            handleClose();
        } catch (error) {
            setError("Registration failed. Please try again.");
            console.error("Register error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const loadUserDetails = async (id: string, username: string, role: "ADMIN" | "USER") => {
        try {
            const userResponse = await getUser();

            const user: User = {
                id: id,
                username: username,
                role: role,
                firstName: userResponse.data.firstName,
                lastName: userResponse.data.lastName,
                email: userResponse.data.email,
                address: userResponse.data.address,
                age: userResponse.data.age,
            };

            localStorage.setItem("user", JSON.stringify(user));
        } catch (error) {
            console.error("Failed to load user details:", error);
            throw error;
        }
    };

    const handleClose = () => {
        setUsername("");
        setPassword("");
        setFirstName("");
        setLastName("");
        setEmail("");
        setAddress("");
        setAge(0);
        setRole("USER");
        setError("");
        setIsRegister(false);
        close();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <Tabs
                value={isRegister ? 1 : 0}
                onChange={(_, val) => setIsRegister(val === 1)}
                variant="fullWidth"
            >
                <Tab label="Login"/>
                <Tab label="Register"/>
            </Tabs>

            {!isRegister ? (
                <form onSubmit={handleSubmitLogin}>
                    <DialogTitle>Login to your account</DialogTitle>
                    <DialogContent>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            autoFocus
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        {error && <Alert severity="error" sx={{mt: 2}}>{error}</Alert>}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>Cancel</Button>
                        <Button type="submit" variant="contained" disabled={isLoading}>
                            {isLoading ? <CircularProgress size={24}/> : "Login"}
                        </Button>
                    </DialogActions>
                </form>
            ) : (
                <form onSubmit={handleSubmitRegister}>
                    <DialogTitle>Register a new account</DialogTitle>
                    <DialogContent>
                        <div style={{display: "flex", gap: 16, flexWrap: "wrap", marginTop: 8}}>
                            <div style={{flex: "1 1 48%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Username"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    autoFocus
                                />
                            </div>
                            <div style={{flex: "1 1 48%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </div>

                            <div style={{flex: "1 1 48%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="First Name"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                />
                            </div>
                            <div style={{flex: "1 1 48%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Last Name"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                />
                            </div>

                            <div style={{flex: "1 1 100%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Email"
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                />
                            </div>

                            <div style={{flex: "1 1 100%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Address"
                                    value={address}
                                    onChange={(e) => setAddress(e.target.value)}
                                />
                            </div>

                            <div style={{flex: "1 1 48%"}}>
                                <TextField
                                    margin="normal"
                                    required
                                    fullWidth
                                    label="Age"
                                    type="number"
                                    inputProps={{min: 0}}
                                    value={age}
                                    onChange={(e) => setAge(Number(e.target.value))}
                                />
                            </div>
                        </div>

                        {error && <Alert severity="error" sx={{mt: 2}}>{error}</Alert>}
                    </DialogContent>

                    <DialogActions>
                        <Button onClick={handleClose}>Cancel</Button>
                        <Button type="submit" variant="contained" disabled={isLoading}>
                            {isLoading ? <CircularProgress size={24}/> : "Register"}
                        </Button>
                    </DialogActions>
                </form>
            )}
        </Dialog>
    );
}

export default AuthDialog;