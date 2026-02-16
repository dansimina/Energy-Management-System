import {useEffect, useState} from "react";
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
import {updateCredentials} from "../api/api.ts";
import type {UserCredentialsDTO} from "../../common/types/UserCredentialsDTO.ts";

interface EditUserDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    id: string;
    credentials: UserCredentialsDTO;
}

function UpdateCredentialsDialog({open, onClose, onSuccess, id, credentials}: EditUserDialogProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState<"USER" | "ADMIN">("USER")
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (open && credentials) {
            setUsername(credentials.username);
            setPassword("");
            setRole(credentials.role);
        }
    }, [credentials, open])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request: UserCredentialsDTO = {
                username: username,
                role: role,
            };

            // Only include password if it's not empty
            if (password.trim()) {
                request.password = password;
            }

            await updateCredentials(id, request);

            onSuccess();
            handleClose();
        } catch (error) {
            setError("Failed to update user. Please try again.");
            console.error("Update user error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        setUsername("");
        setPassword("");
        setRole("USER");
        setError("");
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>Update User Credentials</DialogTitle>
                <DialogContent>
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
                            fullWidth
                            label="Password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Leave blank to keep current password"
                            helperText="Leave blank to keep the current password"
                        />
                    </div>

                    <div style={{flex: "1 1 100%"}}>
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
                        {isLoading ? <CircularProgress size={24}/> : "Update"}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}

export default UpdateCredentialsDialog;