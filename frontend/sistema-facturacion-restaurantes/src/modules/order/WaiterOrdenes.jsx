import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import ConfirmModal    from "../../global/components/ConfirmModal.jsx";
import OrderForm       from "./components/OrderForm.jsx";
import OrderView       from "./components/OrderView.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import { getOrders, cancelOrder, getOrderById } from "./orderService.js";

const ESTADO_BADGE = {
  PENDIENTE:      <Badge variant="danger">Pendiente</Badge>,
  EN_PREPARACION: <Badge variant="warning">En preparación</Badge>,
  LISTO:          <Badge variant="success">Listo</Badge>,
  LISTA:          <Badge variant="success">Listo</Badge>,
  CANCELADO:      <Badge variant="danger">Cancelado</Badge>,
  CANCELADA:      <Badge variant="danger">Cancelado</Badge>,
  PAGADO:         <Badge variant="info">Facturado</Badge>,
  FACTURADA:      <Badge variant="info">Facturado</Badge>,
};

export default function MeseroOrdenes() {
  const [ordenes,     setOrdenes]     = useState([]);
  const [loading,     setLoading]     = useState(false);
  const [showCreate,  setShowCreate]  = useState(false);
  const [showEdit,    setShowEdit]    = useState(false);
  const [showView,    setShowView]    = useState(false);
  const [showCancel,  setShowCancel]  = useState(false);
  const [selected,    setSelected]    = useState(null);
  const [selectedDetail, setSelectedDetail] = useState(null);

  const fetchOrdenes = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getOrders({ page: 0, size: 50 });
      // Filtra las que no están canceladas ni facturadas para el mesero
      const content = (data.content ?? []).filter(
        (o) => o.estado !== "CANCELADO" && o.estado !== "CANCELADA" && o.estado !== "PAGADO" && o.estado !== "FACTURADA"
      );
      setOrdenes(content);
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
  }, []);

  useEffect(() => { fetchOrdenes(); }, [fetchOrdenes]);

  // WebSocket: escucha eventos en tiempo real del topic /topic/ordenes
  useWebSocket("/topic/ordenes", () => { fetchOrdenes(); });

  const openEdit   = (o) => { setSelected(o); setShowEdit(true);   };
  const openView   = async (o) => {
    try {
      const detail = await getOrderById(o.id);
      setSelectedDetail(detail);
      setSelected(o);
      setShowView(true);
    } catch {
      setSelectedDetail(null);
      setSelected(o);
      setShowView(true);
    }
  };
  const openCancel = (o) => { setSelected(o); setShowCancel(true); };

  const handleCancel = async () => {
    try {
      await cancelOrder(selected.id);
      setShowCancel(false);
      await Swal.fire({
        icon: "success",
        title: "Orden cancelada",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchOrdenes();
    } catch (err) {
      setShowCancel(false);
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo cancelar la orden.",
        confirmButtonColor: "#E87722",
      });
    }
  };

  const fmt = (amount) =>
    amount !== undefined
      ? `$${Number(amount).toLocaleString("es-CO")}`
      : "-";

  const rows = ordenes.map((o) => {
    const canEdit   = o.estado === "PENDIENTE";
    const canCancel = o.estado === "PENDIENTE" || o.estado === "EN_PREPARACION";
    return [
      `#${String(o.id).padStart(3, "0")}`,
      `Mesa ${o.numeroMesa}`,
      "—",           // El summary no incluye platos count; se ve en detalle
      "—",           // subtotal se ve en detalle
      ESTADO_BADGE[o.estado] ?? <Badge>{o.estado}</Badge>,
      <div className="flex gap-1.5">
        <Button small variant="ghost" onClick={() => openView(o)}>Ver</Button>
        {canEdit   && <Button small onClick={() => openEdit(o)}>Editar</Button>}
        {canCancel && <Button small variant="danger" onClick={() => openCancel(o)}>Cancelar</Button>}
      </div>,
    ];
  });

  return (
    <DashboardLayout screenName="Mis Órdenes" activeItem="ordenes">
      <PageHeader
        title="Mis Órdenes"
        actionLabel="+ Nueva Orden"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando órdenes...</p>
        ) : (
          <DataTable
            columns={["# Orden", "Mesa", "Platos", "Subtotal", "Estado", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showCreate && (
        <Modal title="Registrar Nueva Orden" onClose={() => setShowCreate(false)}>
          <OrderForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => { setShowCreate(false); fetchOrdenes(); }}
          />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Orden #${String(selected.id).padStart(3, "0")}`} onClose={() => setShowEdit(false)}>
          <OrderForm
            isEdit
            ordenId={selected.id}
            onCancel={() => setShowEdit(false)}
            onSuccess={() => { setShowEdit(false); fetchOrdenes(); }}
          />
        </Modal>
      )}

      {showView && selected && (
        <OrderView orden={selectedDetail ?? selected} onClose={() => setShowView(false)} />
      )}

      {showCancel && selected && (
        <ConfirmModal
          title="Cancelar Orden"
          message={`¿Estás seguro de que deseas cancelar la orden #${String(selected.id).padStart(3, "0")} de Mesa ${selected.numeroMesa}? Esta acción no se puede deshacer.`}
          confirmLabel="Sí, cancelar orden"
          onConfirm={handleCancel}
          onClose={() => setShowCancel(false)}
        />
      )}
    </DashboardLayout>
  );
}
