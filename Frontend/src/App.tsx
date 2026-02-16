import { BrowserRouter, Routes, Route } from "react-router-dom";
import { WebSocketProvider } from "./context/WebSocketProvider";
import NotificationToast from "./context/components/NotificationToast";
import UsersPage from "./users.page/UsersPage";
import UserPage from "./user.page/UserPage";
import DevicesPage from "./devices.page/DevicesPage";
import ProfilePage from "./myprofile.page/ProfilePage";
import ChatPage from "./chat.page/ChatPage";
import ProtectedRoute from "./common/components/ProtectedRoute";
import MainPage from "./main.page/MainPage";
import EnergyConsumptionPage from "./user.energy.consumption.page/EnergyConsumptionPage";
import {
    PATH_HOME, PATH_USERS, PATH_USER_DETAIL, PATH_DEVICES,
    PATH_PROFILE, PATH_CONSUMPTION, PATH_CHAT
} from "./common/Constants";

function App() {
    return (
        <BrowserRouter>
            <WebSocketProvider>
                <NotificationToast />
                <Routes>
                    <Route path={PATH_HOME} element={<MainPage />} />
                    <Route path={PATH_USERS} element={<ProtectedRoute requiredRole={["ADMIN"]}><UsersPage /></ProtectedRoute>} />
                    <Route path={PATH_USER_DETAIL} element={<ProtectedRoute requiredRole={["ADMIN"]}><UserPage /></ProtectedRoute>} />
                    <Route path={PATH_DEVICES} element={<ProtectedRoute requiredRole={["ADMIN"]}><DevicesPage /></ProtectedRoute>} />
                    <Route path={PATH_PROFILE} element={<ProtectedRoute requiredRole={["ADMIN", "USER"]}><ProfilePage /></ProtectedRoute>} />
                    <Route path={PATH_CONSUMPTION} element={<ProtectedRoute requiredRole={["ADMIN", "USER"]}><EnergyConsumptionPage /></ProtectedRoute>} />
                    <Route path={PATH_CHAT} element={<ProtectedRoute requiredRole={["ADMIN", "USER"]}><ChatPage /></ProtectedRoute>} />
                </Routes>
            </WebSocketProvider>
        </BrowserRouter>
    );
}

export default App;