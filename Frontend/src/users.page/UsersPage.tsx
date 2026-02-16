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
import type {UserDTO} from "../common/types/UserDTO.ts";
import {getUsers} from "./api/api.ts";
import {useNavigate} from "react-router-dom";
import AddUserDialog from "./components/AddUserDialog.tsx";
import {getUserDetailsPath} from "../common/Constants.ts";

function UsersPage() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [users, setUsers] = useState([]);
    const [dialogOpen, setDialogOpen] = useState(false);

    const fetchUsers = async () => {
        try {
            const response = await getUsers();
            setUsers(response.data);
        } catch (error) {
            console.error("Error fetching users:", error);
        }
    };

    const fetchUsersWithDelay = async (delayMs: number = 1000) => {
        await new Promise(resolve => setTimeout(resolve, delayMs));
        await fetchUsers();
    };

    useEffect(() => {
        fetchUsers();
    }, [])

    return (
        <Box sx={{display: "flex", flexDirection: "column", minHeight: "100vh"}}>
            <AppNavigationBar/>

            <Container component="main" sx={{flexGrow: 1, py: 4}}>
                <Typography variant="h4" component="h1" gutterBottom sx={{mb: 4}}>
                    Users Management
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
                                onClick={() => setDialogOpen(true)}>
                            ADD USER
                        </Button>
                    </Grid>
                </Grid>

                {/* Users Grid */}
                <Grid container>
                    {users?.length === 0 ? (
                        <Box sx={{height: '100%', width: '100%'}}>
                            <Typography variant="body1" color="text.secondary" textAlign="center">
                                No users found
                            </Typography>
                        </Box>
                    ) : (
                        users
                            ?.filter((user: UserDTO) =>
                                (`${user.firstName} ${user.lastName}`).toLowerCase().includes(searchQuery.toLowerCase())
                            )
                            .map((user: UserDTO) => (
                                <Box key={user.id} sx={{mb: 2, width: '100%'}}>
                                    <Card sx={{height: "100%"}}>
                                        <CardContent>
                                            <Typography variant="h6" gutterBottom>
                                                {user.firstName} {user.lastName}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Email: {user.email}
                                            </Typography>
                                        </CardContent>
                                        <CardActions>
                                            <Button size="small"
                                                    onClick={() => navigate(getUserDetailsPath(user.id))}>
                                                View
                                            </Button>
                                        </CardActions>
                                    </Card>
                                </Box>
                            ))
                    )}
                </Grid>
            </Container>

            {/* Add User Dialog */}
            <AddUserDialog
                open={dialogOpen}
                onClose={() => setDialogOpen(false)}
                onSuccess={() => {
                    setDialogOpen(false);
                    fetchUsersWithDelay();
                }}
            />
        </Box>
    );
}

export default UsersPage;