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
import type {DeviceDTO} from "../../common/types/DeviceDTO.ts";
import {createDevice, updateDevice} from "../api/api.ts";

interface AddDeviceDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    device?: DeviceDTO | null;
}

function AddDeviceDialog({open, onClose, onSuccess, device}: AddDeviceDialogProps) {
    const isEditMode = !!device;

    const [name, setName] = useState("");
    const [maximumConsumptionValue, setMaximumConsumptionValue] = useState(0);
    const [energyClass, setEnergyClass] = useState<"A+++" | "A++" | "A+" | "A" | "B" | "C" | "D" | "E" | "F" | "G">("A+++");
    const [description, setDescription] = useState("");

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (open) {
            if (device) {
                setName(device.name);
                setMaximumConsumptionValue(device.maximumConsumptionValue);
                setEnergyClass(device.energyClass);
                setDescription(device.description);
            } else {
                // Create mode - reset to defaults
                setName("");
                setMaximumConsumptionValue(0);
                setEnergyClass("A+++");
                setDescription("");
            }
            setError("");
        }
    }, [device, open]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");

        try {
            const request: DeviceDTO = {
                id: device?.id || null,
                name: name,
                maximumConsumptionValue: maximumConsumptionValue,
                energyClass: energyClass,
                description: description,
            };

            if (isEditMode && device?.id) {
                await updateDevice(device.id, request);
            } else {
                await createDevice(request);
            }

            onSuccess();
            onClose();
        } catch (error) {
            setError(isEditMode ? "Failed to update device. Please try again." : "Failed to create device. Please try again.");
            console.error("Device operation error:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        // Reset state when closing
        setName("");
        setMaximumConsumptionValue(0);
        setEnergyClass("A+++");
        setDescription("");
        setError("");
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>{isEditMode ? "Edit Device" : "Add New Device"}</DialogTitle>
                <DialogContent>
                    <div style={{display: "flex", gap: 16, flexWrap: "wrap", marginTop: 8}}>
                        <div style={{flex: "1 1 48%"}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                label="Name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                autoFocus
                            />
                        </div>
                        <div style={{flex: "1 1 48%"}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                label="Max Consumption Value"
                                type="text"
                                value={maximumConsumptionValue === 0 ? "" : String(maximumConsumptionValue)}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                                    const digits = e.target.value.replace(/\D/g, "");
                                    setMaximumConsumptionValue(digits ? Number(digits) : 0);
                                }}
                            />
                        </div>
                        <div style={{flex: "1 1 100%"}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                label="Description"
                                value={description}
                                onChange={(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => setDescription(e.target.value)}
                                multiline
                                rows={4}
                                placeholder="Enter device description"
                                variant="outlined"
                            />
                        </div>
                        <div style={{flex: "1 1 100%"}}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                select
                                label="Energetic Class"
                                value={energyClass}
                                onChange={(e) => setEnergyClass(e.target.value as "A+++" | "A++" | "A+" | "A" | "B" | "C" | "D" | "E" | "F" | "G")}
                            >
                                {["A+++", "A++", "A+", "A", "B", "C", "D", "E", "F", "G"].map(
                                    (energyClass) => (
                                        <MenuItem key={energyClass} value={energyClass}>
                                            {energyClass}
                                        </MenuItem>
                                    )
                                )}
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
                        {isLoading ? <CircularProgress size={24}/> : (isEditMode ? "Update" : "Create")}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}

export default AddDeviceDialog;