import {useState, useEffect} from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    CircularProgress,
    Alert,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Box,
    Typography,
    Chip,
} from "@mui/material";
import type {DeviceDTO} from "../../common/types/DeviceDTO.ts";
import {addDeviceToUser, getAvailableDevices} from "../api/api.ts";

interface AddDeviceToUserDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    userId: string;
}

function AddDeviceToUserDialog({
                                   open,
                                   onClose,
                                   onSuccess,
                                   userId,
                               }: AddDeviceToUserDialogProps) {
    const [selectedDeviceId, setSelectedDeviceId] = useState<string>("");
    const [availableDevices, setAvailableDevices] = useState<DeviceDTO[]>([]);

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchDevices = async () => {
        try {
            const responseAvailableDevices = await getAvailableDevices();
            setAvailableDevices(responseAvailableDevices.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    useEffect(() => {
        if (open) {
            setSelectedDeviceId("");
            setError("");
            fetchDevices();
        }
    }, [open]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!selectedDeviceId) {
            setError("Please select a device");
            return;
        }

        setIsLoading(true);
        setError("");

        try {
            addDeviceToUser(userId, selectedDeviceId);

            onSuccess();
            handleClose();
        } catch (error) {
            setError("Failed to add device to user. Please try again.");
            console.error("Add device to user error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        setSelectedDeviceId("");
        setError("");
        onClose();
    };

    const selectedDevice = availableDevices.find(
        (device) => device.id === selectedDeviceId
    );

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>Add Device to User</DialogTitle>
                <DialogContent>
                    <Box sx={{mt: 2}}>
                        {availableDevices.length === 0 ? (
                            <Alert severity="info">
                                No available devices to assign. All devices are already assigned to this user.
                            </Alert>
                        ) : (
                            <>
                                <FormControl fullWidth required>
                                    <InputLabel>Select Device</InputLabel>
                                    <Select
                                        value={selectedDeviceId}
                                        label="Select Device"
                                        onChange={(e) => setSelectedDeviceId(e.target.value)}
                                    >
                                        {availableDevices.map((device) => (
                                            <MenuItem key={device.id || ''} value={device.id || ''}>
                                                {device.name} - {device.energyClass}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>

                                {/* Device Details Preview */}
                                {selectedDevice && (
                                    <Box
                                        sx={{
                                            mt: 3,
                                            p: 2,
                                            border: 1,
                                            borderColor: "divider",
                                            borderRadius: 1,
                                        }}
                                    >
                                        <Typography variant="subtitle2" gutterBottom>
                                            Device Details:
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            <strong>Name:</strong> {selectedDevice.name}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            <strong>Max Consumption:</strong>{" "}
                                            {selectedDevice.maximumConsumptionValue}W
                                        </Typography>
                                        <Box sx={{mt: 1}}>
                                            <Chip
                                                label={selectedDevice.energyClass}
                                                size="small"
                                                color="primary"
                                            />
                                        </Box>
                                        <Typography
                                            variant="body2"
                                            color="text.secondary"
                                            sx={{mt: 1}}
                                        >
                                            {selectedDevice.description}
                                        </Typography>
                                    </Box>
                                )}
                            </>
                        )}
                    </Box>

                    {error && <Alert severity="error" sx={{mt: 2}}>{error}</Alert>}
                </DialogContent>

                <DialogActions>
                    <Button onClick={handleClose} disabled={isLoading}>
                        Cancel
                    </Button>
                    <Button
                        type="submit"
                        variant="contained"
                        disabled={isLoading || availableDevices.length === 0}
                    >
                        {isLoading ? <CircularProgress size={24}/> : "Add Device"}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}

export default AddDeviceToUserDialog;