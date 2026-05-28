import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import UserForm        from "./components/UserForm.jsx";

const USERS = [
  { id: 1, nombre: "Juan",   apellidos: "García",    email: "juan@sfr.com",   rol: "Administrador", activo: true  },
  { id: 2, nombre: "Laura",  apellidos: "Martínez",  email: "laura@sfr.com",  rol: "Mesero",        activo: true  },
  { id: 3, nombre: "Carlos", apellidos: "Rodríguez", email: "carlos@sfr.com", rol: "Chef",          activo: true  },
  { id: 4, nombre: "Ana",    apellidos: "López",     email: "ana@sfr.com",    rol: "Cajero",        activo: false },
];

export default function Usuarios() {
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [showView,   setShowView]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  const openEdit = (u) => { setSelected(u); setShowEdit(true); };
  const openView = (u) => { setSelected(u); setShowView(true); };

  const rows = USERS.map((u) => [
    u.id,
    u.nombre,
    u.apellidos,
    u.rol,
    u.activo
      ? <Badge variant="success">Activo</Badge>
      : <Badge>Inactivo</Badge>,
    <div className="flex gap-1.5">
      <Button small variant="ghost" onClick={() => openView(u)}>Ver</Button>
      <Button small onClick={() => openEdit(u)}>Editar</Button>
      {u.activo
        ? <Button small variant="danger">Desactivar</Button>
        : <Button small variant="success">Reactivar</Button>}
    </div>,
  ]);

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
            { options: ["Todos los roles", "Administrador", "Mesero", "Chef", "Cajero"] },
            { options: ["Todos", "Activos", "Inactivos"] },
          ]}
        />
        <DataTable
          columns={["ID", "Nombre", "Apellidos", "Rol", "Estado", "Acciones"]}
          rows={rows}
        />
      </div>

      {showCreate && (
        <Modal title="Registrar Nuevo Usuario" onClose={() => setShowCreate(false)}>
          <UserForm onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Usuario — ${selected.nombre} ${selected.apellidos}`} onClose={() => setShowEdit(false)}>
          <UserForm isEdit onCancel={() => setShowEdit(false)} />
        </Modal>
      )}

      {showView && selected && (
        <Modal title="Detalle de Usuario" onClose={() => setShowView(false)} size="sm">
          <div className="divide-y divide-gray-100">
            {[
              ["Nombre",    selected.nombre    ],
              ["Apellidos", selected.apellidos ],
              ["Rol",       selected.rol       ],
            ].map(([label, val]) => (
              <div key={label} className="flex justify-between py-3 text-sm">
                <span className="text-gray-500 font-medium">{label}</span>
                <span className="font-bold text-gray-800">{val}</span>
              </div>
            ))}
            <div className="flex justify-between py-3 text-sm">
              <span className="text-gray-500 font-medium">Estado</span>
              {selected.activo
                ? <Badge variant="success">Activo</Badge>
                : <Badge>Inactivo</Badge>}
            </div>
          </div>
        </Modal>
      )}
    </DashboardLayout>
  );
}

