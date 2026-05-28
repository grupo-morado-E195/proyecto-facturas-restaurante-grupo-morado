import DashboardLayout from "../templates/DashboardLayout.jsx";
import StatCard        from "../global/components/StatCard.jsx";
import DataTable       from "../global/components/DataTable.jsx";
import Badge           from "../global/components/Badge.jsx";

const STATS = [
  { label: "Órdenes Hoy",      value: "24",         subtitle: "+3 en la última hora",                      accentColor: "#E87722" },
  { label: "Ventas Hoy",       value: "$1.240.000",  subtitle: "8 órdenes facturadas",                     accentColor: "#2E9E5B" },
  { label: "Mesas Ocupadas",   value: "7 / 12",      subtitle: "5 mesas disponibles",                      accentColor: "#2E7DB5" },
  { label: "Usuarios Activos", value: "5",           subtitle: "1 admin · 2 meseros · 1 chef · 1 cajero",  accentColor: "#E8A020" },
];

const ORDER_ROWS = [
  ["#042", "Mesa 3", "Laura M.",  "$85.000",  <Badge variant="success">Lista</Badge>],
  ["#041", "Mesa 7", "Carlos R.", "$120.000", <Badge variant="warning">En preparación</Badge>],
  ["#040", "Mesa 1", "Laura M.",  "$65.000",  <Badge variant="info">Facturada</Badge>],
  ["#039", "Mesa 5", "Carlos R.", "$95.000",  <Badge variant="info">Facturada</Badge>],
];

export default function AdminDashboard() {
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
          rows={ORDER_ROWS}
        />
      </div>
    </DashboardLayout>
  );
}
