import {useEffect, useState} from "react";
import {
    Box,
    Container,
    TextField,
    Typography,
    Card,
    CardContent,
    Grid,
    Button,
    CardActions,
} from "@mui/material";
import AppNavigationBar from "../common/components/AppNavigationBar";
import {deleteDevice, getDevices} from "./api/api.ts";
import AddDeviceDialog from "./components/AddDeviceDialog.tsx";
import type {DeviceDTO} from "../common/types/DeviceDTO.ts";

function DevicePage() {
    const [searchQuery, setSearchQuery] = useState("");
    const [devices, setDevices] = useState<DeviceDTO[]>([]);
    const [selectedDevice, setSelectedDevice] = useState<DeviceDTO | null>(null);
    const [dialogOpen, setDialogOpen] = useState(false);

    const fetchDevices = async () => {
        try {
            const response = await getDevices();
            setDevices(response.data);
        } catch (error) {
            console.error("Error fetching devices:", error);
        }
    };

    useEffect(() => {
        fetchDevices();
    }, [])

    const handleAddDevice = () => {
        setSelectedDevice(null);
        setDialogOpen(true)
    }

    const handleEditDevice = (device: DeviceDTO) => {
        setSelectedDevice(device);
        setDialogOpen(true);
    }

    const handleDeleteDevice = async (id: string | null) => {
        try {
            if (id != null) {
                await deleteDevice(id);
            }
            fetchDevices();
        } catch (error) {
            console.error("Error deleting device:", error);
        }
    }

    return (
        <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
            <AppNavigationBar/>

            <Container component="main" sx={{flexGrow: 1, py: 4}}>
                <Typography variant="h4" component="h1" gutterBottom sx={{mb: 4}}>
                    Devices Management
                </Typography>

                {/* Search Bar */}
                <Grid container spacing={2} alignItems="stretch" justifyContent="space-between" mb={4}>
                    <Grid size={8}>
                        <Box sx={{display: 'flex', alignItems: 'center', height: '100%'}}>
                            <TextField
                                fullWidth
                                placeholder="Search by first name or last name..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                        </Box>
                    </Grid>
                    <Grid size={4} sx={{display: 'flex'}}>
                        <Button variant="contained" fullWidth sx={{height: '100%'}}
                                onClick={() => handleAddDevice()}>
                            ADD DEVICE
                        </Button>
                    </Grid>
                </Grid>

                {/* Devices Grid */}
                <Grid container>
                    {devices?.length === 0 ? (
                        <Box sx={{height: '100%', width: '100%'}}>
                            <Typography variant="body1" color="text.secondary" textAlign="center">
                                No devices found
                            </Typography>
                        </Box>
                    ) : (
                        devices
                            ?.filter((device: DeviceDTO) =>
                                device.name.toLowerCase().includes(searchQuery.toLowerCase())
                            )
                            .map((device: DeviceDTO) => (
                                <Box key={device.id} sx={{mb: 2, width: '100%'}}>
                                    <Card sx={{height: "100%"}}>
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                {device.name}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Serial: {device.id}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Max Consumption Value: {device.maximumConsumptionValue}W
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Energetic Class: {device.energyClass}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Description: {device.description}
                                            </Typography>
                                        </CardContent>
                                        <CardActions>
                                            <Button size="small" onClick={() => (handleEditDevice(device))}>
                                                Edit
                                            </Button>
                                            <Button size="small" color="error"
                                                    onClick={() => handleDeleteDevice(device.id)}>
                                                Delete
                                            </Button>
                                        </CardActions>
                                    </Card>
                                </Box>
                            ))
                    )}
                </Grid>
            </Container>

            {/* Add Device Dialog */}
            <AddDeviceDialog
                open={dialogOpen}
                onClose={() => setDialogOpen(false)}
                onSuccess={() => {
                    setDialogOpen(false);
                    fetchDevices();
                }}
                device={selectedDevice}
            />
        </Box>
    );
}

export default DevicePage;