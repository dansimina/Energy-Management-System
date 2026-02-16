import {useState} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    CircularProgress,
    Alert,
    MenuItem,
} from "@mui/material";
import {createUser} from "../api/api.ts";

interface AddUserDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

function AddUserDialog({open, onClose, onSuccess}: AddUserDialogProps) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [age, setAge] = useState(0);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState<"USER" | "ADMIN">("USER");
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request = {
                credentials: {username, password, role},
                user: {firstName, lastName, email, address, age}
            };
            await createUser(request);

            onSuccess();
            handleClose();
        } catch (error) {
            setError("Failed to create user. Please try again.");
            console.error("Create user error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        setFirstName("");
        setLastName("");
        setEmail("");
        setAddress("");
        setAge(0);
        setUsername("");
        setPassword("");
        setRole("USER");
        setError("");
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>Add New User</DialogTitle>
                <DialogContent>
                    <div style={{display: "flex", gap: 16, flexWrap: "wrap", marginTop: 8}}>
                        {/* Credentials Section */}
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

                        {/* Personal Information */}
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
                                value={age}
                                onChange={(e) => setAge(Number(e.target.value))}
                            />
                        </div>

                        <div style={{flex: "1 1 48%"}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                select
                                label="Role"
                                value={role}
                                onChange={(e) => setRole(e.target.value as "USER" | "ADMIN")}
                            >
                                <MenuItem value="USER">User</MenuItem>
                                <MenuItem value="ADMIN">Admin</MenuItem>
                            </TextField>
                        </div>
                    </div>

                    {error && <Alert severity="error" sx={{mt: 2}}>{error}</Alert>}
                </DialogContent>

                <DialogActions>
                    <Button onClick={handleClose} disabled={isLoading}>
                        Cancel
                    </Button>
                    <Button
                        type="submit"
                        variant="contained"
                        disabled={isLoading}
                    >
                        {isLoading ? <CircularProgress size={24}/> : "Create User"}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}

export default AddUserDialog;