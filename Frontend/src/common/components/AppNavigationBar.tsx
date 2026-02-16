import { useState, useEffect } from "react";
import {
    AppBar,
    Box,
    Toolbar,
    Typography,
    Button,
    Avatar,
    Menu,
    MenuItem,
    Divider,
} from "@mui/material";
import LoginIcon from "@mui/icons-material/Login";
import BatteryChargingFullIcon from "@mui/icons-material/BatteryChargingFull";
import { useNavigate } from "react-router-dom";
import AuthDialog from "./AuthDialog.tsx";
import NotificationBell from "../../context/components/NotificationBell.tsx";
import { useWebSocket } from "../../context/useWebSocketContext.ts";
import type { User } from "../types/User.ts";
import { PATH_CHAT, PATH_DEVICES, PATH_HOME, PATH_PROFILE, PATH_USERS } from "../Constants.ts";

function AppNavigationBar() {
    const navigate = useNavigate();
    const { reconnect, disconnect } = useWebSocket();
    const [user, setUser] = useState<User | null>(null);
    const [authOpen, setAuthOpen] = useState(false);
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser && storedUser !== "null") {
            try {
                setUser(JSON.parse(storedUser));
            } catch (e) {
                console. error("Invalid user in localStorage:", e);
                setUser(null);
            }
        }
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("user");
        localStorage. removeItem("token");
        disconnect();
        setUser(null);
        setAnchorEl(null);
        navigate(PATH_HOME);
    };

    const updateUser = () => {
        const stored = localStorage.getItem("user");
        if (stored && stored !== "null") {
            try {
                setUser(JSON.parse(stored));
                reconnect();
            } catch {
                setUser(null);
            }
        }
    };

    return (
        <>
            <AppBar position="static">
                <Toolbar>
                    <BatteryChargingFullIcon sx={{ mr: 1 }} />
                    <Typography
                        variant="h6"
                        sx={{
                            fontFamily: "monospace",
                            fontWeight: 700,
                            cursor: "pointer",
                        }}
                        onClick={() => navigate(PATH_HOME)}
                    >
                        Energy System
                    </Typography>

                    <Box sx={{ flexGrow: 1, ml: 3 }}>
                        {user && (
                            <>
                                <Button color="inherit" onClick={() => navigate(PATH_PROFILE)}>
                                    My Profile
                                </Button>
                                <Button color="inherit" onClick={() => navigate(PATH_CHAT)}>
                                    Chat
                                </Button>
                                {user.role === "ADMIN" && (
                                    <>
                                        <Button color="inherit" onClick={() => navigate(PATH_USERS)}>
                                            Users
                                        </Button>
                                        <Button color="inherit" onClick={() => navigate(PATH_DEVICES)}>
                                            Devices
                                        </Button>
                                    </>
                                )}
                            </>
                        )}
                    </Box>

                    {user ? (
                        <>
                            <NotificationBell />

                            <Button
                                color="inherit"
                                onClick={(e) => setAnchorEl(e.currentTarget)}
                                startIcon={
                                    <Avatar
                                        sx={{
                                            bgcolor: user.role === "ADMIN" ?  "red" : "purple",
                                            width: 32,
                                            height: 32,
                                        }}
                                    >
                                        {user.firstName?.[0]?.toUpperCase()}
                                        {user.lastName?.[0]?. toUpperCase()}
                                    </Avatar>
                                }
                            >
                                {user. firstName} {user. lastName}
                            </Button>
                            <Menu
                                anchorEl={anchorEl}
                                open={Boolean(anchorEl)}
                                onClose={() => setAnchorEl(null)}
                            >
                                <Box sx={{ px: 2, py: 1.5 }}>
                                    <Typography variant="body2" color="text. secondary">
                                        {user.email}
                                    </Typography>
                                </Box>
                                <Divider />
                                <MenuItem sx={{ color: "error.main" }} onClick={handleLogout}>
                                    Logout
                                </MenuItem>
                            </Menu>
                        </>
                    ) : (
                        <Button
                            variant="contained"
                            startIcon={<LoginIcon />}
                            onClick={() => setAuthOpen(true)}
                            sx={{ bgcolor: "purple" }}
                        >
                            Login
                        </Button>
                    )}
                </Toolbar>
            </AppBar>

            <AuthDialog open={authOpen} close={() => setAuthOpen(false)} success={updateUser} />
        </>
    );
}

export default AppNavigationBar;