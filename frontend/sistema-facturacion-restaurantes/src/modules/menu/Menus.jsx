import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import MenuForm        from "./components/MenuForm.jsx";

const MENUS = [
  { id: 1, nombre: "Desayunos", activo: true  },
  { id: 2, nombre: "Almuerzos", activo: true  },
  { id: 3, nombre: "Cenas",     activo: true  },
  { id: 4, nombre: "Bebidas",   activo: true  },
  { id: 5, nombre: "Postres",   activo: false },
];

export default function Menus() {
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  const openEdit = (m) => { setSelected(m); setShowEdit(true); };

  const rows = MENUS.map((m) => [
    m.id,
    m.nombre,
    m.activo
      ? <Badge variant="success">Activo</Badge>
      : <Badge>Inactivo</Badge>,
    <div className="flex gap-1.5">
      <Button small onClick={() => openEdit(m)}>Editar</Button>
      {m.activo
        ? <Button small variant="danger">Desactivar</Button>
        : <Button small variant="success">Reactivar</Button>}
    </div>,
  ]);

  return (
    <DashboardLayout screenName="Gestión de Menús" activeItem="menus">
      <PageHeader
        title="Gestión de Menús"
        actionLabel="+ Crear Menú"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <DataTable
          columns={["ID", "Nombre del Menú", "Estado", "Acciones"]}
          rows={rows}
        />
      </div>

      {showCreate && (
        <Modal title="Crear Menú" onClose={() => setShowCreate(false)} size="sm">
          <MenuForm onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Menú — ${selected.nombre}`} onClose={() => setShowEdit(false)} size="sm">
          <MenuForm isEdit onCancel={() => setShowEdit(false)} />
        </Modal>
      )}
    </DashboardLayout>
  );
}

