import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute.jsx";
import PublicRoute    from "./PublicRoute.jsx";
import { ROUTES } from "../constants/routes.js";
import { ROLES }  from "../constants/roles.js";

import Login from "../../modules/auth/Login.jsx";

import AdminDashboard   from "../../pages/AdminDashboard.jsx";
import CashierDashboard from "../../pages/CashierDashboard.jsx";
import ChefDashboard    from "../../pages/ChefDashboard.jsx";
import WaiterDashboard  from "../../pages/WaiterDashboard.jsx";

import Usuarios      from "../../modules/user/Usuarios.jsx";
import Mesas         from "../../modules/table/Mesas.jsx";
import Inventario    from "../../modules/inventary/Inventario.jsx";
import Menus         from "../../modules/menu/Menus.jsx";
import AdminOrdenes  from "../../modules/order/AdminOrdenes.jsx";
import AdminInformes from "../../modules/report/AdminInformes.jsx";

import MeseroOrdenes from "../../modules/order/WaiterOrdenes.jsx";

import Facturacion    from "../../modules/facturation/Facturacion.jsx";
import CajeroInformes from "../../modules/report/CashierInformes.jsx";

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to={ROUTES.LOGIN} replace />} />

      <Route element={<PublicRoute />}>
        <Route path={ROUTES.LOGIN} element={<Login />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.ADMIN]} />}>
        <Route path={ROUTES.ADMIN_DASHBOARD}  element={<AdminDashboard />} />
        <Route path={ROUTES.ADMIN_USUARIOS}   element={<Usuarios />} />
        <Route path={ROUTES.ADMIN_MESAS}      element={<Mesas />} />
        <Route path={ROUTES.ADMIN_INVENTARIO} element={<Inventario />} />
        <Route path={ROUTES.ADMIN_MENUS}      element={<Menus />} />
        <Route path={ROUTES.ADMIN_ORDENES}    element={<AdminOrdenes />} />
        <Route path={ROUTES.ADMIN_INFORMES}   element={<AdminInformes />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.MESERO]} />}>
        <Route path={ROUTES.MESERO_DASHBOARD} element={<WaiterDashboard />} />
        <Route path={ROUTES.MESERO_ORDENES}   element={<MeseroOrdenes />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.CHEF]} />}>
        <Route path={ROUTES.CHEF_DASHBOARD} element={<ChefDashboard />} />
      </Route>

      <Route element={<ProtectedRoute allowedRoles={[ROLES.CAJERO]} />}>
        <Route path={ROUTES.CAJERO_DASHBOARD}   element={<CashierDashboard />} />
        <Route path={ROUTES.CAJERO_FACTURACION} element={<Facturacion />} />
        <Route path={ROUTES.CAJERO_INFORMES}    element={<CajeroInformes />} />
      </Route>

      <Route path="*" element={<Navigate to={ROUTES.LOGIN} replace />} />
    </Routes>
  );
}
