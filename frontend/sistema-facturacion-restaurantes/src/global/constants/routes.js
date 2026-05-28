export const ROUTES = {
  LOGIN: "/login",

  ADMIN_DASHBOARD:  "/admin",
  ADMIN_USUARIOS:   "/admin/usuarios",
  ADMIN_MESAS:      "/admin/mesas",
  ADMIN_INVENTARIO: "/admin/inventario",
  ADMIN_MENUS:      "/admin/menus",
  ADMIN_ORDENES:    "/admin/ordenes",
  ADMIN_INFORMES:   "/admin/informes",

  MESERO_DASHBOARD: "/mesero",
  MESERO_ORDENES:   "/mesero/ordenes",

  CHEF_DASHBOARD: "/chef",

  CAJERO_DASHBOARD:    "/cajero",
  CAJERO_FACTURACION:  "/cajero/facturacion",
  CAJERO_INFORMES:     "/cajero/informes",
};

export function getDashboardByRole(role) {
  const map = {
    admin:  ROUTES.ADMIN_DASHBOARD,
    mesero: ROUTES.MESERO_DASHBOARD,
    chef:   ROUTES.CHEF_DASHBOARD,
    cajero: ROUTES.CAJERO_DASHBOARD,
  };
  return map[role] ?? ROUTES.LOGIN;
}
