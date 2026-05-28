import DashboardLayout from "../templates/DashboardLayout.jsx";
import StatCard        from "../global/components/StatCard.jsx";
import Button          from "../global/components/Button.jsx";

const ORDENES_LISTAS = [
  { id: "#042", mesa: 3, total: "$85.000"  },
  { id: "#038", mesa: 9, total: "$120.000" },
];

const RESUMEN = [
  { label: "Órdenes facturadas", val: "8"           },
  { label: "Total ventas",       val: "$1.240.000"  },
  { label: "Plato más vendido",  val: "Bandeja Paisa" },
];

const STATS = [
  { label: "Órdenes Facturadas", value: "8",          subtitle: "durante el día", accentColor: "#2E9E5B" },
  { label: "Total Ventas",       value: "$1.240.000",  subtitle: "caja del día",   accentColor: "#E87722" },
];

export default function CajeroDashboard() {
  return (
    <DashboardLayout screenName="Panel Cajero" activeItem="home">
      <h1 className="text-xl font-black text-gray-900 mb-5">Panel Cajero</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-5">
        {STATS.map((s) => (
          <StatCard key={s.label} {...s} />
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">
            Órdenes Listas para Facturar
          </h2>
          <div className="divide-y divide-gray-100">
            {ORDENES_LISTAS.map((o) => (
              <div key={o.id} className="flex justify-between items-center py-3">
                <div>
                  <p className="font-bold text-gray-900 text-sm">
                    {o.id} · Mesa {o.mesa}
                  </p>
                  <p className="text-xs text-[#E87722] font-bold mt-0.5">{o.total}</p>
                </div>
                <Button small>Facturar</Button>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">Resumen del Día</h2>
          <div className="divide-y divide-gray-100">
            {RESUMEN.map((r) => (
              <div key={r.label} className="flex justify-between py-3 text-sm">
                <span className="text-gray-500">{r.label}</span>
                <span className="font-black text-gray-800">{r.val}</span>
              </div>
            ))}
          </div>
          <div className="mt-4">
            <Button variant="success" fullWidth>
              Cerrar Caja y Generar Informe
            </Button>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
