import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import ConfirmModal    from "../../global/components/ConfirmModal.jsx";
import OrderForm       from "./components/OrderForm.jsx";
import OrderView       from "./components/OrderView.jsx";

const ORDENES = [
  { id: "#042", mesa: "Mesa 3", platos: 2, subtotal: "$85.000",  estado: "lista"          },
  { id: "#041", mesa: "Mesa 7", platos: 3, subtotal: "$120.000", estado: "en_preparacion" },
  { id: "#038", mesa: "Mesa 2", platos: 1, subtotal: "$32.000",  estado: "en_preparacion" },
];

const ESTADO_BADGE = {
  en_preparacion: <Badge variant="warning">En preparación</Badge>,
  lista:          <Badge variant="success">Lista</Badge>,
  cancelada:      <Badge variant="danger">Cancelada</Badge>,
};

export default function MeseroOrdenes() {
  const [showCreate,  setShowCreate]  = useState(false);
  const [showEdit,    setShowEdit]    = useState(false);
  const [showView,    setShowView]    = useState(false);
  const [showCancel,  setShowCancel]  = useState(false);
  const [selected,    setSelected]    = useState(null);

  const openEdit   = (o) => { setSelected(o); setShowEdit(true);   };
  const openView   = (o) => { setSelected(o); setShowView(true);   };
  const openCancel = (o) => { setSelected(o); setShowCancel(true); };

  const rows = ORDENES.map((o) => [
    o.id,
    o.mesa,
    `${o.platos} plato${o.platos !== 1 ? "s" : ""}`,
    o.subtotal,
    ESTADO_BADGE[o.estado],
    <div className="flex gap-1.5">
      <Button small variant="ghost" onClick={() => openView(o)}>Ver</Button>
      {o.estado !== "lista" && <Button small onClick={() => openEdit(o)}>Editar</Button>}
      {o.estado !== "lista" && (
        <Button small variant="danger" onClick={() => openCancel(o)}>Cancelar</Button>
      )}
    </div>,
  ]);

  return (
    <DashboardLayout screenName="Mis Órdenes" activeItem="ordenes">
      <PageHeader
        title="Mis Órdenes"
        actionLabel="+ Nueva Orden"
        onAction={() => setShowCreate(true)}
      />

      <div className="bg-white rounded-xl p-5 shadow-sm">
        <DataTable
          columns={["# Orden", "Mesa", "Platos", "Subtotal", "Estado", "Acciones"]}
          rows={rows}
        />
      </div>

      {showCreate && (
        <Modal title="Registrar Nueva Orden" onClose={() => setShowCreate(false)}>
          <OrderForm onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {showEdit && selected && (
        <Modal title={`Editar Orden ${selected.id}`} onClose={() => setShowEdit(false)}>
          <OrderForm onCancel={() => setShowEdit(false)} />
        </Modal>
      )}

      {showView && selected && (
        <OrderView orden={selected} onClose={() => setShowView(false)} />
      )}

      {showCancel && selected && (
        <ConfirmModal
          title="Cancelar Orden"
          message={`¿Estás seguro de que deseas cancelar la orden ${selected.id} de ${selected.mesa}? Esta acción no se puede deshacer.`}
          confirmLabel="Sí, cancelar orden"
          onConfirm={() => setShowCancel(false)}
          onClose={() => setShowCancel(false)}
        />
      )}
    </DashboardLayout>
  );
}

