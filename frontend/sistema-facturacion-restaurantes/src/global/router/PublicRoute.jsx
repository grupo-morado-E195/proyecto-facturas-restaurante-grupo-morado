import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { getDashboardByRole } from "../constants/routes.js";

export default function PublicRoute() {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={getDashboardByRole(user.rol)} replace />;
  }

  return <Outlet />;
}
