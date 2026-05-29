import { useState, useEffect, useCallback } from "react";
import DashboardLayout from "../templates/DashboardLayout.jsx";
import PageHeader      from "../global/components/PageHeader.jsx";
import Badge           from "../global/components/Badge.jsx";
import Button          from "../global/components/Button.jsx";
import Modal           from "../global/components/Modal.jsx";
import OrderForm       from "../modules/order/components/OrderForm.jsx";
import OrderView       from "../modules/order/components/OrderView.jsx";
import { getTables }   from "../modules/table/tableService.js";
import { getOrders, getOrderById } from "../modules/order/orderService.js";
import { useWebSocket } from "../global/hooks/useWebSocket.js";
import Swal from "sweetalert2";

export default function MeseroDashboard() {
  const [tables, setTables] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);

  const [showCreate, setShowCreate] = useState(false);
  const [showView, setShowView] = useState(false);
  const [selectedOrderDetail, setSelectedOrderDetail] = useState(null);
  const [loadingAction, setLoadingAction] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [tablesRes, ordersRes] = await Promise.all([
        getTables(0, 100),
        getOrders({ page: 0, size: 100 }),
      ]);
      
      // Filtramos solo mesas ACTIVAS en orden ascendente por número
      const activeTables = (tablesRes.content ?? [])
        .filter((t) => t.status === "ACTIVO")
        .sort((a, b) => a.number - b.number);
      setTables(activeTables);

      // Filtramos las órdenes activas (no facturadas ni canceladas)
      const activeOrders = (ordersRes.content ?? []).filter(
        (o) => o.estado !== "CANCELADA" && o.estado !== "FACTURADA"
      );
      setOrders(activeOrders);
    } catch (err) {
      console.error("Error al cargar mesas/órdenes:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // WebSocket: Escucha cambios en las órdenes en tiempo real para refrescar la vista
  useWebSocket("/topic/ordenes", () => { fetchData(); });

  const handleVerOrden = async (activeOrder, e) => {
    e.stopPropagation(); // Evita clicks redundantes en la tarjeta
    if (!activeOrder) return;
    
    setLoadingAction(true);
    try {
      const detail = await getOrderById(activeOrder.id);
      setSelectedOrderDetail(detail);
      setShowView(true);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo cargar el detalle de la orden.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoadingAction(false);
    }
  };

  const handleNuevaOrden = (e) => {
    e.stopPropagation();
    setShowCreate(true);
  };

  return (
    <DashboardLayout screenName="Vista de Mesas" activeItem="mesas">
      <PageHeader
        title="Vista de Mesas"
        subtitle="Selecciona una mesa para gestionar su orden"
      />

      {loading && tables.length === 0 ? (
        <p className="text-center text-gray-400 py-12 text-sm">Cargando mesas...</p>
      ) : tables.length === 0 ? (
        <p className="text-center text-gray-400 py-12 text-sm">No hay mesas registradas o activas en el sistema.</p>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {tables.map((m) => {
            const activeOrder = orders.find((o) => o.numeroMesa === m.number);
            const isOcupada = m.disponibility === "OCUPADA" || !!activeOrder;
            
            return (
              <div
                key={m.id}
                onClick={(e) => {
                  if (isOcupada) {
                    handleVerOrden(activeOrder, e);
                  } else {
                    handleNuevaOrden(e);
                  }
                }}
                className={`rounded-xl p-4 text-center cursor-pointer transition-all duration-150
                  border-2 shadow-sm hover:shadow-md active:scale-95
                  ${isOcupada
                    ? "bg-orange-50 border-[#E87722] shadow-orange-100/50"
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

                <p className="font-black text-gray-900 text-sm mb-1">Mesa {m.number}</p>
                <div className="mb-2">
                  {isOcupada
                    ? <Badge variant="warning">Ocupada</Badge>
                    : <Badge variant="success">Libre</Badge>}
                </div>
                {activeOrder && (
                  <p className="text-xs text-gray-500 mb-2">Orden #{String(activeOrder.id).padStart(3, "0")}</p>
                )}
                {isOcupada ? (
                  <Button
                    small
                    variant="ghost"
                    disabled={loadingAction}
                    onClick={(e) => handleVerOrden(activeOrder, e)}
                  >
                    {loadingAction ? "Abriendo..." : "Ver Orden"}
                  </Button>
                ) : (
                  <Button
                    small
                    onClick={(e) => handleNuevaOrden(e)}
                  >
                    Nueva Orden
                  </Button>
                )}
              </div>
            );
          })}
        </div>
      )}

      {showCreate && (
        <Modal title="Registrar Nueva Orden" onClose={() => setShowCreate(false)}>
          <OrderForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => {
              setShowCreate(false);
              fetchData();
            }}
          />
        </Modal>
      )}

      {showView && selectedOrderDetail && (
        <OrderView orden={selectedOrderDetail} onClose={() => setShowView(false)} />
      )}
    </DashboardLayout>
  );
}
