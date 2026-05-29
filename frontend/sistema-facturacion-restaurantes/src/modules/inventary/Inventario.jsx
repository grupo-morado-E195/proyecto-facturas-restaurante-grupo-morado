import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import DishForm        from "../dish/components/DishForm.jsx";
import DishView        from "../dish/components/DishView.jsx";
import { useWebSocket } from "../../global/hooks/useWebSocket.js";
import {
  getDishes,
  deactivateDish,
  reactivateDish,
} from "../dish/dishService.js";

export default function Inventario() {
  const [platos,        setPlatos]        = useState([]);
  const [loading,       setLoading]       = useState(false);
  const [showCreate,    setShowCreate]    = useState(false);
  const [showEdit,      setShowEdit]      = useState(false);
  const [showView,      setShowView]      = useState(false);
  const [selectedPlato, setSelectedPlato] = useState(null);

  // Filtros locales
  const [search,      setSearch]      = useState("");
  const [filterStatus, setFilterStatus] = useState("Todos");

  const fetchPlatos = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getDishes({ page: 0, size: 100 });
      setPlatos(data.content ?? []);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudieron cargar los platos.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchPlatos(); }, [fetchPlatos]);

  // WebSocket: escucha eventos en tiempo real del topic /topic/platos
  useWebSocket("/topic/platos", () => { fetchPlatos(); });

  const openEdit = (p) => { setSelectedPlato(p); setShowEdit(true); };
  const openView = (p) => { setSelectedPlato(p); setShowView(true); };

  const handleToggleStatus = async (p) => {
    const isActive = p.status === "ACTIVO";
    const result = await Swal.fire({
      icon:               "warning",
      title:              isActive ? "¿Desactivar plato?" : "¿Reactivar plato?",
      text:               isActive
        ? `Se desactivará "${p.name}".`
        : `Se reactivará "${p.name}".`,
      showCancelButton:   true,
      confirmButtonColor: isActive ? "#D64035" : "#22C55E",
      cancelButtonColor:  "#6B7280",
      confirmButtonText:  isActive ? "Sí, desactivar" : "Sí, reactivar",
      cancelButtonText:   "Cancelar",
    });
    if (!result.isConfirmed) return;

    try {
      if (isActive) {
        await deactivateDish(p.id);
      } else {
        await reactivateDish(p.id);
      }
      await Swal.fire({
        icon: "success",
        title: isActive ? "Plato desactivado" : "Plato reactivado",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchPlatos();
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudo completar la operación.",
        confirmButtonColor: "#E87722",
      });
    }
  };

  // Filtrado local
  const filtered = platos.filter((p) => {
    const nameMatch = p.name?.toLowerCase().includes(search.toLowerCase());
    const statusMatch =
      filterStatus === "Todos" ||
      (filterStatus === "Activos"   && p.status === "ACTIVO")  ||
      (filterStatus === "Inactivos" && p.status !== "ACTIVO");
    return nameMatch && statusMatch;
  });

  const fmt = (price) =>
    price !== undefined
      ? `$${Number(price).toLocaleString("es-CO")}`
      : "-";

  const rows = filtered.map((p) => {
    const isActive = p.status === "ACTIVO";
    return [
      p.id,
      p.name,
      `Menú ${p.menuId}`,
      <span className={p.stock === 0 ? "text-red-600 font-bold" : ""}>{p.stock}</span>,
      fmt(p.price),
      isActive ? <Badge variant="success">Activo</Badge> : <Badge>Inactivo</Badge>,
      <div className="flex gap-1.5">
        <Button small variant="ghost" onClick={() => openView(p)}>Ver</Button>
        <Button small onClick={() => openEdit(p)}>Editar</Button>
        {isActive
          ? <Button small variant="danger"  onClick={() => handleToggleStatus(p)}>Desactivar</Button>
          : <Button small variant="success" onClick={() => handleToggleStatus(p)}>Reactivar</Button>}
      </div>,
    ];
  });

  return (
    <DashboardLayout screenName="Inventario de Platos" activeItem="inventario">
      <PageHeader
        title="Inventario de Platos"
        actionLabel="+ Registrar Plato"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <SearchBar
          placeholder="Buscar plato..."
          filters={[
            { options: ["Todos", "Activos", "Inactivos"], onChange: setFilterStatus },
          ]}
          onSearch={setSearch}
        />
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando platos...</p>
        ) : (
          <DataTable
            columns={["ID", "Nombre", "Menú", "Stock", "Precio", "Estado", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showCreate && (
        <Modal title="Registrar Plato" onClose={() => setShowCreate(false)}>
          <DishForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => { setShowCreate(false); fetchPlatos(); }}
          />
        </Modal>
      )}

      {showEdit && selectedPlato && (
        <Modal title={`Editar Plato — ${selectedPlato.name}`} onClose={() => setShowEdit(false)}>
          <DishForm
            isEdit
            initialData={selectedPlato}
            onCancel={() => setShowEdit(false)}
            onSuccess={() => { setShowEdit(false); fetchPlatos(); }}
          />
        </Modal>
      )}

      {showView && selectedPlato && (
        <DishView plato={selectedPlato} onClose={() => setShowView(false)} />
      )}
    </DashboardLayout>
  );
}
