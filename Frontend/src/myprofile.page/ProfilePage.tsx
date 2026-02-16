import {useState, useEffect} from "react";
import {
    Box,
    Container,
    Typography,
    Card,
    CardContent,
    Grid,
    Divider,
    CircularProgress,
    Chip,
    Avatar,
    Paper, Button,
} from "@mui/material";
import BatteryChargingFullIcon from "@mui/icons-material/BatteryChargingFull";
import DevicesIcon from "@mui/icons-material/Devices";
import BoltIcon from "@mui/icons-material/Bolt";
import EnergySavingsLeafIcon from '@mui/icons-material/EnergySavingsLeaf';
import VisibilityIcon from "@mui/icons-material/Visibility";
import AppNavigationBar from "../common/components/AppNavigationBar";
import type {UserDTO} from "../common/types/UserDTO";
import type {User} from "../common/types/User";
import type {DeviceDTO} from "../common/types/DeviceDTO";
import {getUser} from "../common/api/auth-api";
import {useNavigate} from "react-router-dom";
import {getUserDevices} from "./api/api.ts";
import {getUserConsumptionPath} from "../common/Constants.ts";

function ProfilePage() {
    const navigate = useNavigate();
    const [user, setUser] = useState<User | null>(null);
    const [userDetails, setUserDetails] = useState<UserDTO | null>(null);
    const [devices, setDevices] = useState<DeviceDTO[]>([]);
    const [loading, setLoading] = useState(true);

    const fetchUserData = async () => {
        setLoading(true);
        try {
            const storedUser = localStorage.getItem("user");
            if (storedUser && storedUser !== "null") {
                const parsedUser: User = JSON.parse(storedUser);
                setUser(parsedUser);

                // Fetch fresh user details
                const response = await getUser();
                setUserDetails(response.data);

                // Fetch user's devices
                if (parsedUser.id != null) {
                    const devicesResponse = await getUserDevices(parsedUser.id);
                    setDevices(devicesResponse.data);
                }
            }
        } catch (error) {
            console.error("Error fetching user data:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUserData();
    }, []);

    // Calculate statistics
    const totalDevices = devices.length;
    const totalConsumption = devices.reduce((sum, device) => sum + device.maximumConsumptionValue, 0);
    const averageConsumption = totalDevices > 0 ? Math.round(totalConsumption / totalDevices) : 0;

    // Count energy efficient devices (A+++, A++, A+, A)
    const efficientDevices = devices.filter(d =>
        ["A+++", "A++", "A+", "A"].includes(d.energyClass)
    ).length;

    // Get energy class color
    const getEnergyClassColor = (energyClass: string) => {
        const colors: { [key: string]: string } = {
            "A+++": "#00C853",
            "A++": "#64DD17",
            "A+": "#AEEA00",
            "A": "#FFD600",
            "B": "#FFAB00",
            "C": "#FF6D00",
            "D": "#DD2C00",
            "E": "#D50000",
            "F": "#C62828",
            "G": "#B71C1C",
        };
        return colors[energyClass] || "#757575";
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

    if (!user || !userDetails) {
        return (
            <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
                <AppNavigationBar/>
                <Container sx={{flexGrow: 1, py: 4}}>
                    <Typography variant="h5" color="error">
                        User not found. Please log in.
                    </Typography>
                </Container>
            </Box>
        );
    }

    return (
        <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
            <AppNavigationBar/>

            <Container component="main" sx={{flexGrow: 1, py: 4}}>
                <Typography variant="h4" component="h1" gutterBottom sx={{mb: 4}}>
                    My Profile
                </Typography>

                {/* Profile Card */}
                <Card>
                    <CardContent>
                        <Box sx={{mb: 3, display: "flex", justifyContent: "space-between", alignItems: "center"}}>
                            <Box sx={{display: "flex", alignItems: "center", gap: 2}}>
                                <Avatar
                                    sx={{
                                        bgcolor: user.role === "ADMIN" ? "error.main" : "primary.main",
                                        width: 64,
                                        height: 64,
                                        fontSize: "1.5rem",
                                    }}
                                >
                                    {user.firstName?.[0]?.toUpperCase()}
                                    {user.lastName?.[0]?.toUpperCase()}
                                </Avatar>
                                <Box>
                                    <Typography variant="h5" component="h2">
                                        {user.firstName} {user.lastName}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        @{user.username}
                                    </Typography>
                                </Box>
                                <Chip
                                    label={user.role}
                                    color={user.role === "ADMIN" ? "error" : "primary"}
                                    size="small"
                                />
                            </Box>
                        </Box>

                        <Divider sx={{mb: 3}}/>

                        {/* User Information Grid */}
                        <Grid container spacing={3}>
                            <Grid size={{xs: 12, md: 6}}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Email
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {userDetails.email}
                                </Typography>
                            </Grid>

                            <Grid size={{xs: 12, md: 6}}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Age
                                </Typography>
                                <Typography variant="body1" sx={{mb: 2}}>
                                    {userDetails.age} years old
                                </Typography>
                            </Grid>

                            <Grid size={{xs: 12}}>
                                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                                    Address
                                </Typography>
                                <Typography variant="body1">
                                    {userDetails.address}
                                </Typography>
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>

                {/* Statistics Cards */}
                <Grid container spacing={3} sx={{mt: 1}}>
                    <Grid size={{xs: 12, sm: 6, md: 3}}>
                        <Paper
                            elevation={2}
                            sx={{
                                p: 3,
                                textAlign: "center",
                                background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                                color: "white"
                            }}
                        >
                            <DevicesIcon sx={{fontSize: 40, mb: 1}}/>
                            <Typography variant="h4" fontWeight="bold">
                                {totalDevices}
                            </Typography>
                            <Typography variant="body2">
                                Total Devices
                            </Typography>
                        </Paper>
                    </Grid>

                    <Grid size={{xs: 12, sm: 6, md: 3}}>
                        <Paper
                            elevation={2}
                            sx={{
                                p: 3,
                                textAlign: "center",
                                background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
                                color: "white"
                            }}
                        >
                            <BoltIcon sx={{fontSize: 40, mb: 1}}/>
                            <Typography variant="h4" fontWeight="bold">
                                {totalConsumption}W
                            </Typography>
                            <Typography variant="body2">
                                Total Consumption
                            </Typography>
                        </Paper>
                    </Grid>

                    <Grid size={{xs: 12, sm: 6, md: 3}}>
                        <Paper
                            elevation={2}
                            sx={{
                                p: 3,
                                textAlign: "center",
                                background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
                                color: "white"
                            }}
                        >
                            <BatteryChargingFullIcon sx={{fontSize: 40, mb: 1}}/>
                            <Typography variant="h4" fontWeight="bold">
                                {averageConsumption}W
                            </Typography>
                            <Typography variant="body2">
                                Average per Device
                            </Typography>
                        </Paper>
                    </Grid>

                    <Grid size={{xs: 12, sm: 6, md: 3}}>
                        <Paper
                            elevation={2}
                            sx={{
                                p: 3,
                                textAlign: "center",
                                background: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)",
                                color: "white"
                            }}
                        >
                            <EnergySavingsLeafIcon sx={{fontSize: 40, mb: 1}}/>
                            <Typography variant="h4" fontWeight="bold">
                                {efficientDevices}
                            </Typography>
                            <Typography variant="body2">
                                Efficient Devices
                            </Typography>
                        </Paper>
                    </Grid>
                </Grid>

                {/* My Devices Section */}
                <Card sx={{mt: 3}}>
                    <CardContent>
                        <Box sx={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            gap: 1,
                            mb: 2
                        }}>
                            <Box sx={{display: "flex", alignItems: "center", gap: 1}}>
                                <DevicesIcon/>
                                <Typography variant="h6">
                                    My Devices ({devices.length})
                                </Typography>
                            </Box>
                            <Button
                                variant="outlined"
                                startIcon={<VisibilityIcon/>}
                                onClick={() => navigate(getUserConsumptionPath(user.id))}
                                size="small"
                            >
                                View Consumption
                            </Button>
                        </Box>
                        <Divider sx={{mb: 3}}/>

                        {devices.length === 0 ? (
                            <Box sx={{py: 4, textAlign: "center"}}>
                                <Typography variant="body1" color="text.secondary">
                                    No devices assigned yet
                                </Typography>
                            </Box>
                        ) : (
                            <Grid container spacing={2}>
                                {devices.map((device) => (
                                    <Grid size={{xs: 12, sm: 6, md: 4}} key={device.id}>
                                        <Card
                                            variant="outlined"
                                            sx={{
                                                height: "100%",
                                                transition: "transform 0.2s, box-shadow 0.2s",
                                                "&:hover": {
                                                    transform: "translateY(-4px)",
                                                    boxShadow: 4,
                                                }
                                            }}
                                        >
                                            <CardContent>
                                                <Box sx={{
                                                    display: "flex",
                                                    justifyContent: "space-between",
                                                    alignItems: "start",
                                                    mb: 2
                                                }}>
                                                    <Typography variant="h6" component="div" noWrap>
                                                        {device.name}
                                                    </Typography>
                                                    <Chip
                                                        label={device.energyClass}
                                                        size="small"
                                                        sx={{
                                                            bgcolor: getEnergyClassColor(device.energyClass),
                                                            color: "white",
                                                            fontWeight: "bold",
                                                        }}
                                                    />
                                                </Box>

                                                <Box sx={{mb: 1}}>
                                                    <Typography variant="body2" color="text.secondary">
                                                        Max Consumption
                                                    </Typography>
                                                    <Box sx={{display: "flex", alignItems: "center", gap: 0.5}}>
                                                        <BoltIcon sx={{fontSize: 20, color: "primary.main"}}/>
                                                        <Typography variant="h6" color="primary">
                                                            {device.maximumConsumptionValue}W
                                                        </Typography>
                                                    </Box>
                                                </Box>

                                                <Typography
                                                    variant="body2"
                                                    color="text.secondary"
                                                    sx={{
                                                        overflow: "hidden",
                                                        textOverflow: "ellipsis",
                                                        display: "-webkit-box",
                                                        WebkitLineClamp: 2,
                                                        WebkitBoxOrient: "vertical",
                                                    }}
                                                >
                                                    Serial: {device.id}
                                                </Typography>

                                                <Typography
                                                    variant="body2"
                                                    color="text.secondary"
                                                    sx={{
                                                        overflow: "hidden",
                                                        textOverflow: "ellipsis",
                                                        display: "-webkit-box",
                                                        WebkitLineClamp: 2,
                                                        WebkitBoxOrient: "vertical",
                                                    }}
                                                >
                                                    Description: {device.description}
                                                </Typography>
                                            </CardContent>
                                        </Card>
                                    </Grid>
                                ))}
                            </Grid>
                        )}
                    </CardContent>
                </Card>
            </Container>
        </Box>
    );
}

export default ProfilePage;