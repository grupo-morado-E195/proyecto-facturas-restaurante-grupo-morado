import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import MenuForm        from "./components/MenuForm.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import {
  getMenus,
  deactivateMenu,
  reactivateMenu,
} from "./menuService.js";

export default function Menus() {
  const [menus,      setMenus]      = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  const fetchMenus = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getMenus(0, 100);
      setMenus(data.content ?? []);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudieron cargar los menús.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchMenus(); }, [fetchMenus]);

  // WebSocket: escucha eventos en tiempo real del topic /topic/menus
  useWebSocket("/topic/menus", () => { fetchMenus(); });

  const openEdit = (m) => { setSelected(m); setShowEdit(true); };

  const handleToggleStatus = async (m) => {
    const isActive = m.status === "ACTIVO";
    const result = await Swal.fire({
      icon:               "warning",
      title:              isActive ? "¿Desactivar menú?" : "¿Reactivar menú?",
      text:               isActive
        ? `Se desactivará el menú "${m.name}".`
        : `Se reactivará el menú "${m.name}".`,
      showCancelButton:   true,
      confirmButtonColor: isActive ? "#D64035" : "#22C55E",
      cancelButtonColor:  "#6B7280",
      confirmButtonText:  isActive ? "Sí, desactivar" : "Sí, reactivar",
      cancelButtonText:   "Cancelar",
    });
    if (!result.isConfirmed) return;

    try {
      if (isActive) {
        await deactivateMenu(m.id);
      } else {
        await reactivateMenu(m.id);
      }
      await Swal.fire({
        icon: "success",
        title: isActive ? "Menú desactivado" : "Menú reactivado",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchMenus();
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo completar la operación.",
        confirmButtonColor: "#E87722",
      });
    }
  };

  const rows = menus.map((m) => {
    const isActive = m.status === "ACTIVO";
    return [
      m.id,
      m.name,
      isActive
        ? <Badge variant="success">Activo</Badge>
        : <Badge>Inactivo</Badge>,
      <div className="flex gap-1.5">
        <Button small onClick={() => openEdit(m)}>Editar</Button>
        {isActive
          ? <Button small variant="danger"  onClick={() => handleToggleStatus(m)}>Desactivar</Button>
          : <Button small variant="success" onClick={() => handleToggleStatus(m)}>Reactivar</Button>}
      </div>,
    ];
  });

  return (
    <DashboardLayout screenName="Gestión de Menús" activeItem="menus">
      <PageHeader
        title="Gestión de Menús"
        actionLabel="+ Crear Menú"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando menús...</p>
        ) : (
          <DataTable
            columns={["ID", "Nombre del Menú", "Estado", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showCreate && (
        <Modal title="Crear Menú" onClose={() => setShowCreate(false)} size="sm">
          <MenuForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => { setShowCreate(false); fetchMenus(); }}
          />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Menú — ${selected.name}`} onClose={() => setShowEdit(false)} size="sm">
          <MenuForm
            isEdit
            initialData={selected}
            onCancel={() => setShowEdit(false)}
            onSuccess={() => { setShowEdit(false); fetchMenus(); }}
          />
        </Modal>
      )}
    </DashboardLayout>
  );
}
