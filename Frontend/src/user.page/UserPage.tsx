import {useState, useEffect} from "react";
import {useParams} from "react-router-dom";
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    Button,
    Grid,
    Divider,
    CircularProgress,
    Chip,
    CardActions,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import AddCircleOutlined from "@mui/icons-material/AddCircleOutlined";
import AppNavigationBar from "../common/components/AppNavigationBar";
import type {UserDTO} from "../common/types/UserDTO";
import {useNavigate} from "react-router-dom";

import {
    deleteUser,
    getUser,
    getUserCredentials,
    getUserDevices,
    removeDeviceFromUser
} from "./api/api.ts";
import type {UserCredentialsDTO} from "../common/types/UserCredentialsDTO.ts";
import type {DeviceDTO} from "../common/types/DeviceDTO.ts";
import AddDeviceToUserDialog from "./components/AddDeviceToUserDialog.tsx";
import EditUserDialog from "./components/EditUserDialog.tsx";
import UpdateCredentialsDialog from "./components/UpdateCredentialsDialog.tsx";
import {PATH_USERS} from "../common/Constants.ts";

function UserPage() {
    const navigate = useNavigate();
    const {id} = useParams<{ id: string }>();
    const [user, setUser] = useState<UserDTO | null>(null);
    const [credentials, setCredentials] = useState<UserCredentialsDTO | null>(null);
    const [devices, setDevices] = useState<DeviceDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [addDeviceDialogOpen, setAddDeviceDialogOpen] = useState(false);
    const [editUserDialogOpen, setEditUserDialogOpen] = useState(false);
    const [updateUserCredentialsDialogOpen, setUpdateUserCredentialsDialogOpen] = useState(false);

    const fetchUserData = async () => {
        if (!id) {
            return;
        }

        try {
            const responseUser = await getUser(id);
            setUser(responseUser.data);

            const responseCredentials = await getUserCredentials(id);
            setCredentials(responseCredentials.data);

            const responseDevices = await getUserDevices(id);
            setDevices(responseDevices.data);
        } catch (error) {
            console.error("Error fetching data:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUserData();
    }, [id]);

    const handleEditUser = () => {
        setEditUserDialogOpen(true);
    };

    const handleCredentials = () => {
        setUpdateUserCredentialsDialogOpen(true);
    };

    const handleDelete = async () => {
        try {
            if (id != null) (
                await deleteUser(id)
            )
            navigate(PATH_USERS);
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };

    const handleRemoveDevice = async (deviceId: string) => {
        if (!id) return;

        try {
            removeDeviceFromUser(id, deviceId);
            fetchUserData();
        } catch (error) {
            console.error("Error removing device:", error);
        }
    };

    if (loading) {
        return (
            <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
                <AppNavigationBar/>
                <Container sx={{flexGrow: 1, display: "flex", justifyContent: "center", alignItems: "center"}}>
                    <CircularProgress/>
                </Container>
            </Box>
        );
    }

    if (!user || !id) {
        return (
            <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
                <AppNavigationBar/>
                <Container sx={{flexGrow: 1, py: 4}}>
                    <Typography variant="h5" color="error">
                        User not found
                    </Typography>
                </Container>
            </Box>
        );
    }

    return (
        <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
            <AppNavigationBar/>

            <Container component="main" sx={{flexGrow: 1, py: 4}}>
                {/* User Details Card */}
                <Card>
                    <CardContent>
                        <Box sx={{mb: 3, display: "flex", justifyContent: "space-between", alignItems: "center"}}>
                            <Box sx={{display: "flex", alignItems: "center", gap: 2}}>
                                <Typography variant="h5" component="h2">
                                    @{credentials?.username}
                                </Typography>
                                {credentials && (
                                    <Chip
                                        label={credentials.role}
                                        color={credentials.role === "ADMIN" ? "error" : "primary"}
                                        size="small"
                                    />
                                )}
                            </Box>
                            <Box sx={{display: "flex", gap: 1}}>
                                <Button
                                    variant="outlined"
                                    startIcon={<AddCircleOutlined/>}
                                    onClick={() => setAddDeviceDialogOpen(true)}
                                >
                                    Add Device
                                </Button>
                                <Button
                                    variant="outlined"
                                    startIcon={<EditIcon/>}
                                    onClick={handleEditUser}
                                >
                                    Edit
                                </Button>
                                <Button
                                    variant="outlined"
                                    startIcon={<EditIcon/>}
                                    onClick={handleCredentials}
                                >
                                    Edit Credentials
                                </Button>
                                <Button
                                    variant="outlined"
                                    color="error"
                                    startIcon={<DeleteIcon/>}
                                    onClick={handleDelete}
                                >
                                    Delete
                                </Button>
                            </Box>
                        </Box>

                        <Divider sx={{mb: 3}}/>

                        {/* User Information Grid */}
                        <Grid container spacing={3}>
                            <Grid size={6}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    First Name
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {user.firstName}
                                </Typography>
                            </Grid>

                            <Grid size={6}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Last Name
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {user.lastName}
                                </Typography>
                            </Grid>

                            <Grid size={6}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Email
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {user.email}
                                </Typography>
                            </Grid>

                            <Grid size={6}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Age
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {user.age}
                                </Typography>
                            </Grid>

                            <Grid>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Address
                                </Typography>
                                <Typography variant="body1">
                                    {user.address}
                                </Typography>
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>

                {/* Devices Section */}
                <Card sx={{mt: 3}}>
                    <CardContent>
                        <Typography variant="h6" gutterBottom>
                            Associated Devices ({devices?.length || 0})
                        </Typography>
                        <Divider sx={{mb: 2}}/>
                        {devices?.length === 0 ? (
                            <Box sx={{height: '100%', width: '100%', py: 4}}>
                                <Typography variant="body1" color="text.secondary" textAlign="center">
                                    No devices assigned to this user
                                </Typography>
                            </Box>
                        ) : (
                            devices.map((device: DeviceDTO) => (
                                <Box key={device.id} sx={{mb: 2, width: '100%'}}>
                                    <Card sx={{height: "100%"}}>
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                {device.name}
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
                                            <Button
                                                size="small"
                                                color="error"
                                                onClick={() => device.id && handleRemoveDevice(device.id)}
                                            >
                                                Remove
                                            </Button>
                                        </CardActions>
                                    </Card>
                                </Box>
                            ))
                        )}
                    </CardContent>
                </Card>
            </Container>

            {/* Add Device Dialog */}
            <AddDeviceToUserDialog
                open={addDeviceDialogOpen}
                onClose={() => setAddDeviceDialogOpen(false)}
                onSuccess={() => {
                    setAddDeviceDialogOpen(false);
                    fetchUserData();
                }}
                userId={id || ""}
            />

            {/* Edit User Dialog */}
            {user && (<EditUserDialog
                    open={editUserDialogOpen}
                    onClose={() => setEditUserDialogOpen(false)}
                    onSuccess={() => {
                        setEditUserDialogOpen(false);
                        fetchUserData();
                    }}
                    id={id}
                    user={user}
                />
            )}

            {/* Update User Credentials Dialog */}
            {credentials && (
                <UpdateCredentialsDialog
                    open={updateUserCredentialsDialogOpen}
                    onClose={() => setUpdateUserCredentialsDialogOpen(false)}
                    onSuccess={() => {
                        setUpdateUserCredentialsDialogOpen(false);
                        fetchUserData();
                    }}
                    id={id}
                    credentials={credentials}
                />
            )}
        </Box>
    );
}

export default UserPage;