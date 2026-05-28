import DashboardLayout from "../templates/DashboardLayout.jsx";
import PageHeader      from "../global/components/PageHeader.jsx";
import Badge           from "../global/components/Badge.jsx";
import Button          from "../global/components/Button.jsx";

const MESAS = [
  { num: 1, estado: "Ocupada", orden: "#041" },
  { num: 2, estado: "Libre",   orden: null   },
  { num: 3, estado: "Ocupada", orden: "#042" },
  { num: 4, estado: "Libre",   orden: null   },
  { num: 5, estado: "Ocupada", orden: "#040" },
  { num: 6, estado: "Libre",   orden: null   },
  { num: 7, estado: "Ocupada", orden: "#039" },
  { num: 8, estado: "Libre",   orden: null   },
];

export default function MeseroDashboard() {
  return (
    <DashboardLayout screenName="Vista de Mesas" activeItem="mesas">
      <PageHeader
        title="Vista de Mesas"
        subtitle="Selecciona una mesa para gestionar su orden"
      />

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
        {MESAS.map((m) => {
          const isOcupada = m.estado === "Ocupada";
          return (
            <div
              key={m.num}
              className={`rounded-xl p-4 text-center cursor-pointer transition-all
                border-2 shadow-sm hover:shadow-md
                ${isOcupada
                  ? "bg-orange-50 border-[#E87722] shadow-orange-100"
                  : "bg-white border-gray-200"
                }`}
            >
              <div
                className={`w-12 h-12 rounded-full mx-auto mb-3 flex items-center justify-center
                ${isOcupada ? "bg-[#E87722]/10" : "bg-gray-100"}`}
              >
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.5"
                  className={`w-6 h-6 ${isOcupada ? "text-[#E87722]" : "text-gray-400"}`}
                >
                  <path strokeLinecap="round" strokeLinejoin="round"
                    d="M3 10h18M3 10V6a1 1 0 011-1h16a1 1 0 011 1v4M3 10l2 10h14l2-10" />
                </svg>
              </div>

              <p className="font-black text-gray-900 text-sm mb-1">Mesa {m.num}</p>
              <div className="mb-2">
                {isOcupada
                  ? <Badge variant="warning">Ocupada</Badge>
                  : <Badge variant="success">Libre</Badge>}
              </div>
              {m.orden && (
                <p className="text-xs text-gray-500 mb-2">Orden {m.orden}</p>
              )}
              {isOcupada
                ? <Button small variant="ghost">Ver Orden</Button>
                : <Button small>Nueva Orden</Button>}
            </div>
          );
        })}
      </div>
    </DashboardLayout>
  );
}
