import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import UserForm        from "./components/UserForm.jsx";
import {
  getUsers,
  getUserById,
  deactivateUser,
  reactivateUser,
} from "./userService.js";

export default function Usuarios() {
  const [users,      setUsers]      = useState([]);
  const [loading,    setLoading]    = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [showView,   setShowView]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  // Filtros locales
  const [search,     setSearch]     = useState("");
  const [filterRole, setFilterRole] = useState("Todos los roles");
  const [filterStatus, setFilterStatus] = useState("Todos");

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getUsers(0, 100);
      setUsers(data.content ?? []);
    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: err.response?.data?.message ?? "No se pudieron cargar los usuarios.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const openEdit = async (u) => {
    try {
      const detail = await getUserById(u.id);
      setSelected(detail);
      setShowEdit(true);
    } catch {
      setSelected(u);
      setShowEdit(true);
    }
  };
  const openView = (u) => { setSelected(u); setShowView(true); };

  const handleToggleStatus = async (u) => {
    const isActive = u.status === "ACTIVO";
    const result = await Swal.fire({
      icon:               "warning",
      title:              isActive ? "¿Desactivar usuario?" : "¿Reactivar usuario?",
      text:               isActive
        ? `Se desactivará a ${u.name} ${u.lastname}. El usuario no podrá iniciar sesión.`
        : `Se reactivará a ${u.name} ${u.lastname}.`,
      showCancelButton:   true,
      confirmButtonColor: isActive ? "#D64035" : "#22C55E",
      cancelButtonColor:  "#6B7280",
      confirmButtonText:  isActive ? "Sí, desactivar" : "Sí, reactivar",
      cancelButtonText:   "Cancelar",
    });
    if (!result.isConfirmed) return;

    try {
      if (isActive) {
        await deactivateUser(u.id);
      } else {
        await reactivateUser(u.id);
      }
      await Swal.fire({
        icon: "success",
        title: isActive ? "Usuario desactivado" : "Usuario reactivado",
        timer: 1500,
        showConfirmButton: false,
      });
      fetchUsers();
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
  const ROLE_LABEL_MAP = {
    admin:          "Administrador",
    administrador:  "Administrador",
    mesero:         "Mesero",
    chef:           "Chef",
    cajero:         "Cajero",
  };

  const filtered = users.filter((u) => {
    const fullName = `${u.name} ${u.lastname}`.toLowerCase();
    const emailMatch = u.email?.toLowerCase().includes(search.toLowerCase());
    const nameMatch  = fullName.includes(search.toLowerCase());
    const roleLabel  = ROLE_LABEL_MAP[u.role?.toLowerCase()] ?? u.role;
    const roleMatch  =
      filterRole === "Todos los roles" ||
      roleLabel?.toLowerCase() === filterRole.toLowerCase();
    const statusMatch =
      filterStatus === "Todos" ||
      (filterStatus === "Activos"   && u.status === "ACTIVO")  ||
      (filterStatus === "Inactivos" && u.status !== "ACTIVO");
    return (nameMatch || emailMatch) && roleMatch && statusMatch;
  });

  const rows = filtered.map((u) => {
    const isActive = u.status === "ACTIVO";
    const roleLabel = ROLE_LABEL_MAP[u.role?.toLowerCase()] ?? u.role;
    return [
      u.id,
      u.name,
      u.lastname,
      roleLabel,
      isActive
        ? <Badge variant="success">Activo</Badge>
        : <Badge>Inactivo</Badge>,
      <div className="flex gap-1.5">
        <Button small variant="ghost" onClick={() => openView(u)}>Ver</Button>
        <Button small onClick={() => openEdit(u)}>Editar</Button>
        {isActive
          ? <Button small variant="danger"  onClick={() => handleToggleStatus(u)}>Desactivar</Button>
          : <Button small variant="success" onClick={() => handleToggleStatus(u)}>Reactivar</Button>}
      </div>,
    ];
  });

  return (
    <DashboardLayout screenName="Gestión de Usuarios" activeItem="usuarios">
      <PageHeader
        title="Gestión de Usuarios"
        actionLabel="+ Registrar Usuario"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <SearchBar
          placeholder="Buscar por nombre o correo..."
          filters={[
            { options: ["Todos los roles", "Administrador", "Mesero", "Chef", "Cajero"], onChange: setFilterRole },
            { options: ["Todos", "Activos", "Inactivos"], onChange: setFilterStatus },
          ]}
          onSearch={setSearch}
        />
        {loading ? (
          <p className="text-center text-gray-400 py-8 text-sm">Cargando usuarios...</p>
        ) : (
          <DataTable
            columns={["ID", "Nombre", "Apellidos", "Rol", "Estado", "Acciones"]}
            rows={rows}
          />
        )}
      </div>

      {showCreate && (
        <Modal title="Registrar Nuevo Usuario" onClose={() => setShowCreate(false)}>
          <UserForm
            onCancel={() => setShowCreate(false)}
            onSuccess={() => { setShowCreate(false); fetchUsers(); }}
          />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Usuario — ${selected.name} ${selected.lastname}`} onClose={() => setShowEdit(false)}>
          <UserForm
            isEdit
            initialData={selected}
            onCancel={() => setShowEdit(false)}
            onSuccess={() => { setShowEdit(false); fetchUsers(); }}
          />
        </Modal>
      )}

      {showView && selected && (
        <Modal title="Detalle de Usuario" onClose={() => setShowView(false)} size="sm">
          <div className="divide-y divide-gray-100">
            {[
              ["Nombre",    selected.name    ],
              ["Apellidos", selected.lastname ],
              ["Correo",    selected.email   ],
              ["Rol",       ROLE_LABEL_MAP[selected.role?.toLowerCase()] ?? selected.role],
            ].map(([label, val]) => (
              <div key={label} className="flex justify-between py-3 text-sm">
                <span className="text-gray-500 font-medium">{label}</span>
                <span className="font-bold text-gray-800">{val}</span>
              </div>
            ))}
            <div className="flex justify-between py-3 text-sm">
              <span className="text-gray-500 font-medium">Estado</span>
              {selected.status === "ACTIVO"
                ? <Badge variant="success">Activo</Badge>
                : <Badge>Inactivo</Badge>}
            </div>
          </div>
        </Modal>
      )}
    </DashboardLayout>
  );
}
