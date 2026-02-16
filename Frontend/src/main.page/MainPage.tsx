import {Box, Container, Typography} from "@mui/material";
import AppNavigationBar from "../common/components/AppNavigationBar";

function MainPage() {
    return (
        <Box sx={{display: "flex", flexDirection: "column", minHeight: "50vh"}}>
            {/* App Bar */}
            <AppNavigationBar/>

            {/* Main content */}
            <Container component="main" sx={{flexGrow: 1, py: 4}}>
                <Box sx={{textAlign: "center", maxWidth: 800, mx: "auto", mt: 8}}>
                    <Typography
                        variant="h3"
                        component="h1"
                        gutterBottom
                        sx={{fontWeight: "bold"}}
                    >
                        Welcome to Energy Management System
                    </Typography>
                    <Typography variant="h5" color="text.secondary" sx={{mb: 6}}>
                        Monitor, control and optimize your energy consumption
                    </Typography>
                </Box>
            </Container>
        </Box>
    );
}

export default MainPage;