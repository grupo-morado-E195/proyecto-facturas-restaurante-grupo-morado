import DashboardLayout from "../templates/DashboardLayout.jsx";
import PageHeader      from "../global/components/PageHeader.jsx";
import Badge           from "../global/components/Badge.jsx";
import Button          from "../global/components/Button.jsx";

const ORDENES = [
  {
    id: "#042",
    mesa: 3,
    platos: ["Bandeja Paisa", "Jugo de Lulo"],
    estado: "pendiente",
    tiempo: "3 min",
  },
  {
    id: "#041",
    mesa: 7,
    platos: ["Ajiaco", "Arepa de Choclo x2"],
    estado: "en_preparacion",
    tiempo: "12 min",
  },
  {
    id: "#039",
    mesa: 5,
    platos: ["Sancocho de Gallina"],
    estado: "lista",
    tiempo: "20 min",
  },
];

const ESTADO_CONFIG = {
  pendiente:      { badge: <Badge variant="danger">Pendiente</Badge>,       accent: "#D64035" },
  en_preparacion: { badge: <Badge variant="warning">En preparación</Badge>, accent: "#E8A020" },
  lista:          { badge: <Badge variant="success">Lista</Badge>,          accent: "#2E9E5B" },
};

export default function ChefDashboard() {
  return (
    <DashboardLayout screenName="Cola de Órdenes" activeItem="ordenes">
      <PageHeader title="Cola de Órdenes" />

      <div className="space-y-3">
        {ORDENES.map((o) => {
          const { badge, accent } = ESTADO_CONFIG[o.estado] ?? {};
          return (
            <div
              key={o.id}
              className="bg-white rounded-xl p-5 shadow-sm flex flex-col sm:flex-row items-start sm:items-center gap-4 border-l-4"
              style={{ borderLeftColor: accent }}
            >
              <div className="flex-1 min-w-0">
                <div className="flex flex-wrap items-center gap-2 mb-1.5">
                  <span className="font-black text-gray-900">{o.id}</span>
                  <span className="text-gray-500 text-sm">Mesa {o.mesa}</span>
                  {badge}
                </div>
                <p className="text-sm text-gray-600 truncate">{o.platos.join(" · ")}</p>
              </div>

              <div className="flex sm:flex-col items-center sm:items-end gap-3">
                <div className="flex items-center gap-1 text-xs text-gray-400">
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="w-3.5 h-3.5">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {o.tiempo}
                </div>
                {o.estado === "pendiente"      && <Button small>Iniciar</Button>}
                {o.estado === "en_preparacion" && <Button small variant="success">Marcar Lista</Button>}
                {o.estado === "lista"          && <Badge variant="success">Entregada</Badge>}
              </div>
            </div>
          );
        })}
      </div>
    </DashboardLayout>
  );
}
