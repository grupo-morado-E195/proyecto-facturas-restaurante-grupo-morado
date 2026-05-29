import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { getDashboardByRole } from "../constants/routes.js";

export default function PublicRoute() {
  const { isAuthenticated, user, requiresPasswordChange } = useAuth();
  const location = useLocation();

  if (isAuthenticated) {
    if (requiresPasswordChange) {
      if (location.pathname === "/update-password") {
        return <Outlet />;
      }
      return <Navigate to="/update-password" replace />;
    }
    return <Navigate to={getDashboardByRole(user.rol)} replace />;
  }

  return <Outlet />;
}
