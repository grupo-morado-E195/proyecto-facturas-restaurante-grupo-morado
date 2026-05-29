import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import TableForm       from "./components/TableForm.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import {
  getTables,
  deactivateTable,
  reactivateTable,
} from "./tableService.js";

export default function Mesas() {
  const [mesas,      setMesas]      = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  const fetchMesas = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getTables(0, 100);
      setMesas(data.content ?? []);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudieron cargar las mesas.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchMesas(); }, [fetchMesas]);

  // WebSocket: escucha eventos en tiempo real del topic /topic/mesas
  useWebSocket("/topic/mesas", () => { fetchMesas(); });

  const openEdit = (m) => { setSelected(m); setShowEdit(true); };

  const handleToggleStatus = async (m) => {
    const isActive = m.status === "ACTIVO";
    const result = await Swal.fire({
      icon:               "warning",
      title:              isActive ? "¿Desactivar mesa?" : "¿Reactivar mesa?",
      text:               isActive
        ? `Se desactivará la Mesa ${m.number}.`
        : `Se reactivará la Mesa ${m.number}.`,
      showCancelButton:   true,
      confirmButtonColor: isActive ? "#D64035" : "#22C55E",
      cancelButtonColor:  "#6B7280",
      confirmButtonText:  isActive ? "Sí, desactivar" : "Sí, reactivar",
      cancelButtonText:   "Cancelar",
    });
    if (!result.isConfirmed) return;

    try {
      if (isActive) {
        await deactivateTable(m.id);
      } else {
        await reactivateTable(m.id);
      }
      await Swal.fire({
        icon: "success",
        title: isActive ? "Mesa desactivada" : "Mesa reactivada",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchMesas();
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo completar la operación.",
        confirmButtonColor: "#E87722",
      });
    }
  };

  const rows = mesas.map((m) => {
    const isDisponible = m.disponibility === "DISPONIBLE";
    const isActive     = m.status === "ACTIVO";
    return [
      m.id,
      `Mesa ${m.number}`,
      isDisponible
        ? <Badge variant="success">Libre</Badge>
        : <Badge variant="warning">Ocupada</Badge>,
      isActive
        ? <Badge variant="success">Activa</Badge>
        : <Badge>Inactiva</Badge>,
      <div className="flex gap-1.5">
        <Button small onClick={() => openEdit(m)}>Editar</Button>
        {isActive
          ? <Button small variant="danger"  onClick={() => handleToggleStatus(m)}>Desactivar</Button>
          : <Button small variant="success" onClick={() => handleToggleStatus(m)}>Reactivar</Button>}
      </div>,
    ];
  });

  return (
    <DashboardLayout screenName="Gestión de Mesas" activeItem="mesas">
      <PageHeader
        title="Gestión de Mesas"
        actionLabel="+ Crear Mesa"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando mesas...</p>
        ) : (
          <DataTable
            columns={["ID", "Número Mesa", "Disponibilidad", "Estado Registro", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showCreate && (
        <Modal title="Crear Mesa" onClose={() => setShowCreate(false)} size="sm">
          <TableForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => { setShowCreate(false); fetchMesas(); }}
          />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Mesa ${selected.number}`} onClose={() => setShowEdit(false)} size="sm">
          <TableForm
            isEdit
            initialData={selected}
            onCancel={() => setShowEdit(false)}
            onSuccess={() => { setShowEdit(false); fetchMesas(); }}
          />
        </Modal>
      )}
    </DashboardLayout>
  );
}
