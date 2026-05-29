import { useState, useEffect, useCallback } from "react";
import DashboardLayout from "../templates/DashboardLayout.jsx";
import StatCard        from "../global/components/StatCard.jsx";
import DataTable       from "../global/components/DataTable.jsx";
import Badge           from "../global/components/Badge.jsx";
import { useWebSocket }  from "../global/hooks/useWebSocket.js";
import { getOrders }     from "../modules/order/orderService.js";
import { getTables }     from "../modules/table/tableService.js";
import { getUsers }      from "../modules/user/userService.js";

const ESTADO_BADGE = {
  PENDIENTE:      <Badge variant="danger">Pendiente</Badge>,
  EN_PREPARACION: <Badge variant="warning">En preparación</Badge>,
  LISTA:          <Badge variant="success">Lista</Badge>,
  FACTURADA:      <Badge variant="info">Facturada</Badge>,
  CANCELADA:      <Badge>Cancelada</Badge>,
};

export default function AdminDashboard() {
  const [stats,       setStats]       = useState({ ordenes: "-", ventas: "-", mesasOcupadas: "-", mesasTotal: "-", usuarios: "-" });
  const [ultimasOrdenes, setUltimasOrdenes] = useState([]);

  const fetchData = useCallback(async () => {
    try {
      const [ordenesRes, mesasRes, usuariosRes] = await Promise.all([
        getOrders({ page: 0, size: 50 }),
        getTables(0, 100),
        getUsers(0, 100),
      ]);

      const ordenes  = ordenesRes.content ?? [];
      const mesas    = mesasRes.content  ?? [];
      const usuarios = usuariosRes.content ?? [];

      const facturadas   = ordenes.filter((o) => o.estado === "FACTURADA");
      const mesasOcupadas = mesas.filter((m) => m.disponibility === "OCUPADA");

      setStats({
        ordenes:      ordenes.length,
        ventas:       facturadas.length,
        mesasOcupadas: mesasOcupadas.length,
        mesasTotal:    mesas.length,
        usuarios:      usuarios.filter((u) => u.status === "ACTIVO").length,
      });

      // Muestra las últimas 5 órdenes
      const ultimas = ordenes.slice(0, 5).map((o) => [
        `#${String(o.id).padStart(3, "0")}`,
        `Mesa ${o.numeroMesa}`,
        o.nombreMesero ?? "—",
        "—",
        ESTADO_BADGE[o.estado] ?? <Badge>{o.estado}</Badge>,
      ]);
      setUltimasOrdenes(ultimas);
    } catch {
      // Silencia errores del dashboard
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  useWebSocket("/topic/ordenes", () => { fetchData(); });

  const STATS = [
    { label: "Órdenes Hoy",      value: String(stats.ordenes),                                subtitle: `${stats.ventas} facturadas`,                           accentColor: "#E87722" },
    { label: "Ventas Hoy",       value: `${stats.ventas} facturas`,                           subtitle: "Órdenes facturadas",                                   accentColor: "#2E9E5B" },
    { label: "Mesas Ocupadas",   value: `${stats.mesasOcupadas} / ${stats.mesasTotal}`,      subtitle: `${Number(stats.mesasTotal) - Number(stats.mesasOcupadas)} mesas disponibles`, accentColor: "#2E7DB5" },
    { label: "Usuarios Activos", value: String(stats.usuarios),                               subtitle: "Usuarios activos en el sistema",                        accentColor: "#E8A020" },
  ];

  return (
    <DashboardLayout screenName="Panel de Administración" activeItem="home">
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-6">
        {STATS.map((s) => (
          <StatCard key={s.label} {...s} />
        ))}
      </div>

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <h2 className="font-bold text-gray-900 mb-4 text-sm">Últimas Órdenes</h2>
        <DataTable
          columns={["# Orden", "Mesa", "Mesero", "Total", "Estado"]}
          rows={ultimasOrdenes.length > 0 ? ultimasOrdenes : [["—", "—", "—", "—", "—"]]}
        />
      </div>
    </DashboardLayout>
  );
}
