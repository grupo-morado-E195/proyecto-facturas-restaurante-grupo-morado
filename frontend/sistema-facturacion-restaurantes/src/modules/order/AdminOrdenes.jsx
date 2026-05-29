import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import OrderView       from "./components/OrderView.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import { getOrders, getOrderById } from "./orderService.js";

const ESTADO_BADGE = {
  PENDIENTE:      <Badge variant="danger">Pendiente</Badge>,
  EN_PREPARACION: <Badge variant="warning">En preparación</Badge>,
  LISTA:          <Badge variant="success">Lista</Badge>,
  FACTURADA:      <Badge variant="info">Facturada</Badge>,
  CANCELADA:      <Badge>Cancelada</Badge>,
};

const ESTADOS_FILTRO = [
  "Todos los estados",
  "Pendiente",
  "En preparación",
  "Lista",
  "Facturada",
  "Cancelada",
];

const ESTADO_MAP = {
  "Pendiente":      "PENDIENTE",
  "En preparación": "EN_PREPARACION",
  "Lista":          "LISTA",
  "Facturada":      "FACTURADA",
  "Cancelada":      "CANCELADA",
};

export default function AdminOrdenes() {
  const [ordenes,     setOrdenes]     = useState([]);
  const [loading,     setLoading]     = useState(false);
  const [showView,    setShowView]    = useState(false);
  const [selected,    setSelected]    = useState(null);
  const [selectedDetail, setSelectedDetail] = useState(null);
  const [filterEstado, setFilterEstado] = useState("Todos los estados");

  const fetchOrdenes = useCallback(async () => {
    setLoading(true);
    try {
      const statusParam =
        filterEstado === "Todos los estados"
          ? null
          : ESTADO_MAP[filterEstado];
      const data = await getOrders({ status: statusParam, page: 0, size: 100 });
      setOrdenes(data.content ?? []);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudieron cargar las órdenes.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, [filterEstado]);

  useEffect(() => { fetchOrdenes(); }, [fetchOrdenes]);

  // WebSocket: escucha eventos en tiempo real del topic /topic/ordenes
  useWebSocket("/topic/ordenes", () => { fetchOrdenes(); });

  const openView = async (o) => {
    try {
      const detail = await getOrderById(o.id);
      setSelectedDetail(detail);
    } catch {
      setSelectedDetail(null);
    }
    setSelected(o);
    setShowView(true);
  };

  const rows = ordenes.map((o) => [
    `#${String(o.id).padStart(3, "0")}`,
    `Mesa ${o.numeroMesa}`,
    o.nombreMesero ?? "—",
    "—",     // platos count en detalle
    "—",     // subtotal en detalle
    ESTADO_BADGE[o.estado] ?? <Badge>{o.estado}</Badge>,
    <Button small variant="ghost" onClick={() => openView(o)}>Ver</Button>,
  ]);

  return (
    <DashboardLayout screenName="Órdenes" activeItem="ordenes">
      <PageHeader title="Órdenes" subtitle="Todas las órdenes del sistema" />
      <div className="bg-white rounded-xl p-5 shadow-sm">
        <SearchBar
          placeholder="Buscar por # orden o mesa..."
          filters={[
            { options: ESTADOS_FILTRO, onChange: setFilterEstado },
          ]}
        />
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando órdenes...</p>
        ) : (
          <DataTable
            columns={["# Orden", "Mesa", "Mesero", "Platos", "Subtotal", "Estado", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showView && selected && (
        <Modal
          title={`Detalle Orden #${String(selected.id).padStart(3, "0")}`}
          onClose={() => setShowView(false)}
        >
          <OrderView orden={selectedDetail ?? selected} onClose={() => setShowView(false)} />
        </Modal>
      )}
    </DashboardLayout>
  );
}
