import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import Button          from "../../global/components/Button.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Modal           from "../../global/components/Modal.jsx";
import BillingForm     from "./components/BillingForm.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import { getOrders, getOrderById } from "../order/orderService.js";

export default function Facturacion() {
  const [ordenesListas,  setOrdenesListas]  = useState([]);
  const [loading,        setLoading]        = useState(false);
  const [showModal,      setShowModal]      = useState(false);
  const [selectedOrden,  setSelectedOrden]  = useState(null);
  const [ordenDetalle,   setOrdenDetalle]   = useState(null);

  const fetchOrdenesListas = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getOrders({ status: "LISTA", page: 0, size: 50 });
      setOrdenesListas(data.content ?? []);
    } catch (err) {
      console.error("No se pudieron cargar las órdenes:", err);
      setOrdenesListas([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchOrdenesListas(); }, [fetchOrdenesListas]);

  // WebSocket: escucha eventos de facturación y de órdenes
  useWebSocket("/topic/ordenes",     () => { fetchOrdenesListas(); });
  useWebSocket("/topic/facturacion", () => { fetchOrdenesListas(); });

  const handleFacturar = async (orden) => {
    try {
      const detalle = await getOrderById(orden.id);
      setSelectedOrden(orden);
      setOrdenDetalle(detalle);
      setShowModal(true);
    } catch {
      setSelectedOrden(orden);
      setOrdenDetalle(null);
      setShowModal(true);
    }
  };

  const fmt = (amount) =>
    amount !== undefined ? `$${Number(amount).toLocaleString("es-CO")}` : "-";

  return (
    <DashboardLayout screenName="Facturación" activeItem="facturacion">
      <PageHeader title="Facturación" />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        {/* Panel izquierdo: lista de órdenes listas */}
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">
            Órdenes Listas para Facturar
          </h2>
          {loading ? (
            <p className="text-center text-gray-400 py-8 text-sm">Cargando órdenes...</p>
          ) : ordenesListas.length === 0 ? (
            <p className="text-center text-gray-400 py-8 text-sm">No hay órdenes listas.</p>
          ) : (
            <div className="divide-y divide-gray-100">
              {ordenesListas.map((o) => (
                <div key={o.id} className="py-4 flex items-center justify-between gap-3">
                  <div>
                    <p className="font-bold text-gray-900 text-sm">
                      #{String(o.id).padStart(3, "0")} · Mesa {o.numeroMesa}
                    </p>
                    <p className="text-xs text-gray-500 mt-0.5">
                      Mesero: {o.nombreMesero ?? "—"}
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <Button small onClick={() => handleFacturar(o)}>Facturar</Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Panel derecho: detalle de la orden seleccionada */}
        <div className="bg-white rounded-xl p-5 shadow-sm">
          {ordenDetalle ? (
            <>
              <div className="flex items-center justify-between mb-1">
                <h2 className="font-bold text-gray-800 text-sm">
                  Detalle Orden #{String(ordenDetalle.id).padStart(3, "0")}
                </h2>
                <Badge variant="success">Lista</Badge>
              </div>
              <p className="text-xs text-gray-500 mb-4">
                Mesa {ordenDetalle.tableNumber}
              </p>

              <div className="divide-y divide-gray-100 mb-4">
                {(ordenDetalle.details ?? []).map((item, i) => (
                  <div key={i} className="flex justify-between py-2.5 text-sm">
                    <span className="text-gray-700">{item.cantidad}x {item.nombrePlato}</span>
                    <span className="font-semibold text-gray-800">{fmt(item.subtotalDetalle)}</span>
                  </div>
                ))}
              </div>

              <div className="border-t-2 border-gray-200 pt-3 space-y-1.5 mb-5">
                {[
                  ["Subtotal",              fmt(ordenDetalle.subtotal),       false],
                  ["Impuesto al consumo (8%)", fmt(ordenDetalle.consumptionTax), false],
                  ["Total",                 fmt(ordenDetalle.total),          true ],
                ].map(([k, v, bold]) => (
                  <div
                    key={k}
                    className={`flex justify-between text-sm ${
                      bold
                        ? "font-black text-[#E87722] text-base border-t border-gray-100 pt-2 mt-1"
                        : "text-gray-600"
                    }`}
                  >
                    <span>{k}</span>
                    <span>{v}</span>
                  </div>
                ))}
              </div>

              <Button fullWidth onClick={() => setShowModal(true)}>
                Confirmar Facturación
              </Button>
            </>
          ) : (
            <p className="text-center text-gray-400 py-12 text-sm">
              Selecciona una orden para ver el detalle.
            </p>
          )}
        </div>
      </div>

      {showModal && selectedOrden && (
        <Modal
          title={`Confirmar Facturación — Orden #${String(selectedOrden.id).padStart(3, "0")}`}
          onClose={() => setShowModal(false)}
        >
          <BillingForm
            ordenId={selectedOrden.id}
            mesa={selectedOrden.numeroMesa}
            mesero={selectedOrden.nombreMesero ?? "—"}
            ordenDetalle={ordenDetalle}
            onCancel={() => setShowModal(false)}
            onSuccess={() => {
              setShowModal(false);
              setOrdenDetalle(null);
              setSelectedOrden(null);
              fetchOrdenesListas();
            }}
          />
        </Modal>
      )}
    </DashboardLayout>
  );
}
