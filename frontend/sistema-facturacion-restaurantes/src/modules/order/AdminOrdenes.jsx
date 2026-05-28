import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import SearchBar       from "../../global/components/SearchBar.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import Button          from "../../global/components/Button.jsx";

const ORDENES = [
  { id: "#042", mesa: "Mesa 3", mesero: "Laura M.",  platos: 2, subtotal: "$85.000",  estado: "lista"          },
  { id: "#041", mesa: "Mesa 7", mesero: "Carlos R.", platos: 3, subtotal: "$120.000", estado: "en_preparacion" },
  { id: "#040", mesa: "Mesa 1", mesero: "Laura M.",  platos: 1, subtotal: "$65.000",  estado: "facturada"      },
  { id: "#039", mesa: "Mesa 5", mesero: "Carlos R.", platos: 2, subtotal: "$95.000",  estado: "facturada"      },
  { id: "#038", mesa: "Mesa 9", mesero: "Carlos R.", platos: 2, subtotal: "$120.000", estado: "pendiente"      },
];

const ESTADO_BADGE = {
  pendiente:      <Badge variant="danger">Pendiente</Badge>,
  en_preparacion: <Badge variant="warning">En preparación</Badge>,
  lista:          <Badge variant="success">Lista</Badge>,
  facturada:      <Badge variant="info">Facturada</Badge>,
};

export default function AdminOrdenes() {
  const rows = ORDENES.map((o) => [
    o.id,
    o.mesa,
    o.mesero,
    `${o.platos} plato${o.platos !== 1 ? "s" : ""}`,
    o.subtotal,
    ESTADO_BADGE[o.estado],
    <Button small variant="ghost">Ver</Button>,
  ]);

  return (
    <DashboardLayout screenName="Órdenes" activeItem="ordenes">
      <PageHeader title="Órdenes" subtitle="Todas las órdenes del sistema" />
      <div className="bg-white rounded-xl p-5 shadow-sm">
        <SearchBar
          placeholder="Buscar por # orden o mesa..."
          filters={[
            { options: ["Todos los estados", "Pendiente", "En preparación", "Lista", "Facturada"] },
            { options: ["Todos los meseros", "Laura Martínez", "Carlos Rodríguez"] },
          ]}
        />
        <DataTable
          columns={["# Orden", "Mesa", "Mesero", "Platos", "Subtotal", "Estado", "Acciones"]}
          rows={rows}
        />
      </div>
    </DashboardLayout>
  );
}
