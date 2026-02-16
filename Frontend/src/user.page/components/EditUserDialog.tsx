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
} from "@mui/material";
import {updateUser} from "../api/api.ts";
import type {UserDTO} from "../../common/types/UserDTO.ts";

interface EditUserDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    id: string;
    user: UserDTO;
}

function EditUserDialog({open, onClose, onSuccess, id, user}: EditUserDialogProps) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [address, setAddress] = useState("");
    const [age, setAge] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (open && user) {
            setFirstName(user.firstName);
            setLastName(user.lastName);
            setEmail(user.email);
            setAddress(user.address);
            setAge(user.age);
        }
    }, [user, open])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request: UserDTO = {
                id: null,
                firstName: firstName,
                lastName: lastName,
                email: email,
                address: address,
                age: age,
            };
            await updateUser(id, request);

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
        setError("");
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>Add New User</DialogTitle>
                <DialogContent>
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

export default EditUserDialog;