import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import TableForm       from "./components/TableForm.jsx";

const MESAS = [
  { id: 1, numero: "Mesa 1", disponibilidad: "Ocupada", registro: "Activa"   },
  { id: 2, numero: "Mesa 2", disponibilidad: "Libre",   registro: "Activa"   },
  { id: 3, numero: "Mesa 3", disponibilidad: "Ocupada", registro: "Activa"   },
  { id: 4, numero: "Mesa 4", disponibilidad: "Libre",   registro: "Inactiva" },
  { id: 5, numero: "Mesa 5", disponibilidad: "Ocupada", registro: "Activa"   },
  { id: 6, numero: "Mesa 6", disponibilidad: "Libre",   registro: "Activa"   },
];

export default function Mesas() {
  const [showCreate, setShowCreate] = useState(false);
  const [showEdit,   setShowEdit]   = useState(false);
  const [selected,   setSelected]   = useState(null);

  const openEdit = (m) => { setSelected(m); setShowEdit(true); };

  const rows = MESAS.map((m) => [
    m.id,
    m.numero,
    m.disponibilidad === "Ocupada"
      ? <Badge variant="warning">Ocupada</Badge>
      : <Badge variant="success">Libre</Badge>,
    m.registro === "Activa"
      ? <Badge variant="success">Activa</Badge>
      : <Badge>Inactiva</Badge>,
    <div className="flex gap-1.5">
      <Button small onClick={() => openEdit(m)}>Editar</Button>
      {m.registro === "Activa"
        ? <Button small variant="danger">Desactivar</Button>
        : <Button small variant="success">Reactivar</Button>}
    </div>,
  ]);

  return (
    <DashboardLayout screenName="Gestión de Mesas" activeItem="mesas">
      <PageHeader
        title="Gestión de Mesas"
        actionLabel="+ Crear Mesa"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <DataTable
          columns={["ID", "Número Mesa", "Disponibilidad", "Estado Registro", "Acciones"]}
          rows={rows}
        />
      </div>

      {showCreate && (
        <Modal title="Crear Mesa" onClose={() => setShowCreate(false)} size="sm">
          <TableForm onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar ${selected.numero}`} onClose={() => setShowEdit(false)} size="sm">
          <TableForm isEdit onCancel={() => setShowEdit(false)} />
        </Modal>
      )}
    </DashboardLayout>
  );
}

