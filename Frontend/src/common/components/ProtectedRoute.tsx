import {Navigate} from "react-router-dom";
import type {User} from "../types/User.ts";

interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredRole?: ("USER" | "ADMIN")[];
}

const ALLOWED_ROLES = ["USER", "ADMIN"];

function ProtectedRoute({children, requiredRole}: ProtectedRouteProps) {
    const userString = localStorage.getItem("user");
    const token = localStorage.getItem("token");

    if (!userString || !token || userString === "null") {
        return <Navigate to={"/"} replace/>
    }

    let user: User;
    try {
        user = JSON.parse(userString);
    } catch (_) {
        return <Navigate to={"/"} replace/>
    }

    if (!ALLOWED_ROLES.includes(user.role) || (requiredRole && !requiredRole.includes(user.role))) {
        return <Navigate to={"/"} replace/>
    }

    return children;
}

export default ProtectedRoute;