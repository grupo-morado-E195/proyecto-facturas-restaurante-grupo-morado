import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { getDashboardByRole, ROUTES } from "../constants/routes.js";

export default function ProtectedRoute({ allowedRoles }) {
  const { isAuthenticated, user, requiresPasswordChange } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />;
  }

  if (requiresPasswordChange) {
    return <Navigate to="/update-password" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.rol)) {
    return <Navigate to={getDashboardByRole(user.rol)} replace />;
  }

  return <Outlet />;
}
