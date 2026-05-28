import { ROUTES } from "./routes.js";

export const SIDEBAR_BY_ROLE = {
  admin: [
    { icon: "⊞",  label: "Inicio",     key: "home",       path: ROUTES.ADMIN_DASHBOARD },
    { icon: "👤", label: "Usuarios",   key: "usuarios",   path: ROUTES.ADMIN_USUARIOS },
    { icon: "◻",  label: "Mesas",      key: "mesas",      path: ROUTES.ADMIN_MESAS },
    { icon: "▦",  label: "Inventario", key: "inventario", path: ROUTES.ADMIN_INVENTARIO },
    { icon: "≡",  label: "Menús",      key: "menus",      path: ROUTES.ADMIN_MENUS },
    { icon: "✎",  label: "Órdenes",    key: "ordenes",    path: ROUTES.ADMIN_ORDENES },
    { icon: "▣",  label: "Informes",   key: "informes",   path: ROUTES.ADMIN_INFORMES },
  ],
  mesero: [
    { icon: "◻", label: "Mis Mesas", key: "mesas",   path: ROUTES.MESERO_DASHBOARD },
    { icon: "✎", label: "Órdenes",   key: "ordenes", path: ROUTES.MESERO_ORDENES },
  ],
  chef: [
    { icon: "✎", label: "Cola de Órdenes", key: "ordenes", path: ROUTES.CHEF_DASHBOARD },
  ],
  cajero: [
    { icon: "▣", label: "Inicio",      key: "home",        path: ROUTES.CAJERO_DASHBOARD },
    { icon: "≡", label: "Facturación", key: "facturacion", path: ROUTES.CAJERO_FACTURACION },
    { icon: "●", label: "Informes",    key: "informes",    path: ROUTES.CAJERO_INFORMES },
  ],
};
