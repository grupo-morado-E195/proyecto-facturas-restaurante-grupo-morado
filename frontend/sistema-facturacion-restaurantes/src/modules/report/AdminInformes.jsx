import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import Button          from "../../global/components/Button.jsx";
import Input           from "../../global/components/Input.jsx";

const RESUMEN = [
  ["Ventas totales",      "$1.240.000"           ],
  ["Órdenes facturadas",  "8"                    ],
  ["Plato más vendido",   "Bandeja Paisa (14 uds)"],
  ["Plato menos vendido", "Sancocho (2 uds)"     ],
  ["Generado por",        "Juan García"          ],
];

const VENTAS_MESERO = [
  { nombre: "Laura Martínez",   ventas: "$640.000", ordenes: 4, pct: 52 },
  { nombre: "Carlos Rodríguez", ventas: "$600.000", ordenes: 4, pct: 48 },
];

export default function AdminInformes() {
  return (
    <DashboardLayout screenName="Informes de Ventas" activeItem="informes">
      <PageHeader title="Informes de Ventas" />

      <div className="bg-white rounded-xl p-5 shadow-sm mb-5">
        <h2 className="font-bold text-gray-800 text-sm mb-4">Generar Informe Diario</h2>
        <div className="flex flex-col sm:flex-row gap-3 items-end">
          <div className="flex-1">
            <Input label="Fecha" type="date" defaultValue="2024-03-08" />
          </div>
          <div className="mb-4">
            <Button>Generar Informe PDF</Button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">Resumen del Día</h2>
          <div className="divide-y divide-gray-100">
            {RESUMEN.map(([k, v]) => (
              <div key={k} className="flex justify-between py-3 text-sm">
                <span className="text-gray-500">{k}</span>
                <span className="font-bold text-gray-800">{v}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">Ventas por Mesero</h2>
          <div className="space-y-4">
            {VENTAS_MESERO.map((m) => (
              <div key={m.nombre}>
                <div className="flex justify-between text-sm mb-1.5">
                  <span className="font-semibold text-gray-800">{m.nombre}</span>
                  <span className="font-black text-[#E87722]">{m.ventas}</span>
                </div>
                <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-[#E87722] rounded-full transition-all"
                    style={{ width: `${m.pct}%` }}
                  />
                </div>
                <p className="text-xs text-gray-400 mt-1">{m.ordenes} órdenes</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
