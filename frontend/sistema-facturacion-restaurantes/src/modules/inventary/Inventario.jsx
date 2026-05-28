import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import DishForm        from "../dish/components/DishForm.jsx";
import DishView        from "../dish/components/DishView.jsx";

const PLATOS = [
  { id: 1, nombre: "Bandeja Paisa",   menu: "Almuerzos", stock: 15, precio: "$38.000", activo: true  },
  { id: 2, nombre: "Ajiaco",          menu: "Almuerzos", stock: 8,  precio: "$32.000", activo: true  },
  { id: 3, nombre: "Arepa de Choclo", menu: "Desayunos", stock: 0,  precio: "$12.000", activo: false },
  { id: 4, nombre: "Jugo de Lulo",    menu: "Bebidas",   stock: 20, precio: "$8.000",  activo: true  },
  { id: 5, nombre: "Sancocho",        menu: "Almuerzos", stock: 6,  precio: "$28.000", activo: true  },
];

export default function Inventario() {
  const [showCreate,   setShowCreate]   = useState(false);
  const [showEdit,     setShowEdit]     = useState(false);
  const [showView,     setShowView]     = useState(false);
  const [selectedPlato, setSelectedPlato] = useState(null);

  const openEdit = (p) => { setSelectedPlato(p); setShowEdit(true); };
  const openView = (p) => { setSelectedPlato(p); setShowView(true); };

  const rows = PLATOS.map((p) => [
    p.id,
    p.nombre,
    p.menu,
    <span className={p.stock === 0 ? "text-red-600 font-bold" : ""}>{p.stock}</span>,
    p.precio,
    p.activo ? <Badge variant="success">Activo</Badge> : <Badge>Inactivo</Badge>,
    <div className="flex gap-1.5">
      <Button small variant="ghost" onClick={() => openView(p)}>Ver</Button>
      <Button small onClick={() => openEdit(p)}>Editar</Button>
      {p.activo
        ? <Button small variant="danger">Desactivar</Button>
        : <Button small variant="success">Reactivar</Button>}
    </div>,
  ]);

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
            { options: ["Todos los menús", "Desayunos", "Almuerzos", "Cenas", "Bebidas", "Postres"] },
            { options: ["Todos", "Activos", "Inactivos"] },
          ]}
        />
        <DataTable
          columns={["ID", "Nombre", "Menú", "Stock", "Precio", "Estado", "Acciones"]}
          rows={rows}
        />
      </div>

      {showCreate && (
        <Modal title="Registrar Plato" onClose={() => setShowCreate(false)}>
          <DishForm onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {showEdit && selectedPlato && (
        <Modal title={`Editar Plato — ${selectedPlato.nombre}`} onClose={() => setShowEdit(false)}>
          <DishForm isEdit onCancel={() => setShowEdit(false)} />
        </Modal>
      )}

      {showView && selectedPlato && (
        <DishView plato={selectedPlato} onClose={() => setShowView(false)} />
      )}
    </DashboardLayout>
  );
}

