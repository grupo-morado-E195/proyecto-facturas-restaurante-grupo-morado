import Modal from "../../../global/components/Modal.jsx";
import Badge from "../../../global/components/Badge.jsx";

export default function DishView({ plato, onClose }) {
  if (!plato) return null;

  const fmt = (price) =>
    price !== undefined
      ? `$${Number(price).toLocaleString("es-CO")}`
      : "-";

  const rows = [
    ["Nombre",      plato.name        ],
    ["Descripción", plato.description ?? "—"],
    ["Menú (ID)",   plato.menuId      ],
    ["Precio",      fmt(plato.price)  ],
    ["Stock",       plato.stock       ],
  ];

  return (
    <Modal title="Detalle del Plato" onClose={onClose} size="sm">
      <div className="divide-y divide-gray-100">
        {rows.map(([label, val]) => (
          <div key={label} className="flex justify-between py-3 text-sm">
            <span className="text-gray-500 font-medium">{label}</span>
            <span className="font-bold text-gray-800">{val}</span>
          </div>
        ))}
        <div className="flex justify-between py-3 text-sm">
          <span className="text-gray-500 font-medium">Estado</span>
          {plato.status === "ACTIVO"
            ? <Badge variant="success">Activo</Badge>
            : <Badge>Inactivo</Badge>}
        </div>
      </div>
    </Modal>
  );
}
