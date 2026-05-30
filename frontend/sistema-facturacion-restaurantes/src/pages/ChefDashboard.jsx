import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../templates/DashboardLayout.jsx";
import PageHeader      from "../global/components/PageHeader.jsx";
import Badge           from "../global/components/Badge.jsx";
import Button          from "../global/components/Button.jsx";
import { useWebSocket }    from "../global/hooks/useWebSocket.js";
import { getOrders, updateOrderStatus, getOrderById } from "../modules/order/orderService.js";
import OrderView from "../modules/order/components/OrderView.jsx";

const ESTADO_CONFIG = {
  PENDIENTE:      { badge: <Badge variant="danger">Pendiente</Badge>,       accent: "#D64035" },
  EN_PREPARACION: { badge: <Badge variant="warning">En preparación</Badge>, accent: "#E8A020" },
  LISTO:          { badge: <Badge variant="success">Listo para entregar</Badge>, accent: "#2E9E5B" },
};

export default function ChefDashboard() {
  const [ordenes,  setOrdenes]  = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [updating, setUpdating] = useState(null);
  const [selectedDetail, setSelectedDetail] = useState(null);
  const [showView, setShowView] = useState(false);

  const fetchOrdenes = useCallback(async () => {
    setLoading(true);
    try {
      // El chef ve las órdenes PENDIENTES, EN_PREPARACION y LISTAS (LISTO)
      const [pendientes, preparacion, listas] = await Promise.all([
        getOrders({ status: "PENDIENTE",      page: 0, size: 20 }),
        getOrders({ status: "EN_PREPARACION", page: 0, size: 20 }),
        getOrders({ status: "LISTO",          page: 0, size: 20 }),
      ]);
      const todas = [
        ...(pendientes.content   ?? []),
        ...(preparacion.content  ?? []),
        ...(listas.content       ?? []),
      ];
      // Ordena: pendientes primero, luego en preparación, luego listas
      const ORDEN = { PENDIENTE: 0, EN_PREPARACION: 1, LISTO: 2 };
      todas.sort((a, b) => (ORDEN[a.estado] ?? 3) - (ORDEN[b.estado] ?? 3));
      setOrdenes(todas);
    } catch {
      // Silencia errores de carga del chef
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchOrdenes(); }, [fetchOrdenes]);

  // WebSocket: recarga cuando lleguen eventos de órdenes
  useWebSocket("/topic/ordenes", () => { fetchOrdenes(); });

  const handleIniciar = async (ordenId) => {
    setUpdating(ordenId);
    try {
      await updateOrderStatus(ordenId, "EN_PREPARACION");
      fetchOrdenes();
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo actualizar el estado.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setUpdating(null);
    }
  };

  const handleMarcarLista = async (ordenId) => {
    setUpdating(ordenId);
    try {
      await updateOrderStatus(ordenId, "LISTO");
      fetchOrdenes();
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo actualizar el estado.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setUpdating(null);
    }
  };

  const handleVerOrden = async (o) => {
    try {
      const detail = await getOrderById(o.id);
      setSelectedDetail(detail);
      setShowView(true);
    } catch {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudo cargar el detalle de la orden.",
        confirmButtonColor: "#E87722",
      });
    }
  };

  if (loading && ordenes.length === 0) {
    return (
      <DashboardLayout screenName="Cola de Órdenes" activeItem="ordenes">
        <PageHeader title="Cola de Órdenes" />
        <p className="text-center text-gray-400 py-12 text-sm">Cargando órdenes...</p>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout screenName="Cola de Órdenes" activeItem="ordenes">
      <PageHeader title="Cola de Órdenes" />

      {ordenes.length === 0 ? (
        <div className="bg-white rounded-xl p-8 shadow-sm text-center">
          <p className="text-gray-400 text-sm">No hay órdenes activas en este momento.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {ordenes.map((o) => {
            const { badge, accent } = ESTADO_CONFIG[o.estado] ?? {};
            const isUpdating = updating === o.id;
            return (
              <div
                key={o.id}
                className="bg-white rounded-xl p-5 shadow-sm flex flex-col sm:flex-row items-start sm:items-center gap-4 border-l-4 cursor-pointer hover:bg-gray-50 transition-colors"
                style={{ borderLeftColor: accent }}
                onClick={() => handleVerOrden(o)}
              >
                <div className="flex-1 min-w-0">
                  <div className="flex flex-wrap items-center gap-2 mb-1.5">
                    <span className="font-black text-gray-900">
                      #{String(o.id).padStart(3, "0")}
                    </span>
                    <span className="text-gray-500 text-sm">Mesa {o.numeroMesa}</span>
                    {badge}
                  </div>
                  <p className="text-sm text-gray-600 truncate">
                    Mesero: {o.nombreMesero ?? "—"}
                  </p>
                </div>

                <div className="flex sm:flex-col items-center sm:items-end gap-3">
                  {o.estado === "PENDIENTE"      && (
                    <Button
                      small
                      onClick={(e) => {
                        e.stopPropagation();
                        handleIniciar(o.id);
                      }}
                      disabled={isUpdating}
                    >
                      {isUpdating ? "..." : "Iniciar"}
                    </Button>
                  )}
                  {o.estado === "EN_PREPARACION" && (
                    <Button
                      small
                      variant="success"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleMarcarLista(o.id);
                      }}
                      disabled={isUpdating}
                    >
                      {isUpdating ? "..." : "Marcar Lista"}
                    </Button>
                  )}
                  {o.estado === "LISTO" && (
                    <Badge variant="success">Listo para entregar</Badge>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {showView && selectedDetail && (
        <OrderView orden={selectedDetail} onClose={() => setShowView(false)} />
      )}
    </DashboardLayout>
  );
}
