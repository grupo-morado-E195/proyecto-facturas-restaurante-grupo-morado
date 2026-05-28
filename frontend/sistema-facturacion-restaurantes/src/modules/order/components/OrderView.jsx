import Modal from "../../../global/components/Modal.jsx";
import Badge from "../../../global/components/Badge.jsx";

const ESTADO_BADGE = {
  en_preparacion: <Badge variant="warning">En preparación</Badge>,
  lista:          <Badge variant="success">Lista</Badge>,
  cancelada:      <Badge variant="danger">Cancelada</Badge>,
  pendiente:      <Badge variant="danger">Pendiente</Badge>,
};

const PLATOS_SAMPLE = [
  { nombre: "Bandeja Paisa",   cantidad: 1, precio: "$38.000" },
  { nombre: "Jugo de Lulo",    cantidad: 2, precio: "$16.000" },
];

export default function OrderView({ orden, onClose }) {
  if (!orden) return null;

  return (
    <Modal title={`Detalle Orden ${orden.id}`} onClose={onClose}>
      <div className="bg-gray-50 rounded-lg px-4 py-3 mb-4 text-sm flex justify-between items-center">
        <div>
          <p className="font-bold text-gray-800">{orden.mesa}</p>
          <p className="text-gray-500 text-xs mt-0.5">
            {orden.platos} plato{orden.platos !== 1 ? "s" : ""}
          </p>
        </div>
        {ESTADO_BADGE[orden.estado]}
      </div>

      <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
        Platos de la orden
      </p>
      <div className="border border-gray-200 rounded-lg overflow-hidden mb-4">
        <div className="grid grid-cols-[1fr_auto_80px] gap-2 px-3 py-2 bg-gray-50 border-b border-gray-200">
          <span className="text-xs font-bold text-gray-500">Plato</span>
          <span className="text-xs font-bold text-gray-500">Cant.</span>
          <span className="text-xs font-bold text-gray-500 text-right">Total</span>
        </div>
        {PLATOS_SAMPLE.map((p) => (
          <div
            key={p.nombre}
            className="grid grid-cols-[1fr_auto_80px] gap-2 items-center px-3 py-2.5 border-b border-gray-100 last:border-0"
          >
            <span className="text-sm text-gray-800">{p.nombre}</span>
            <span className="text-xs text-gray-500 text-center">{p.cantidad}</span>
            <span className="text-sm font-semibold text-gray-800 text-right">{p.precio}</span>
          </div>
        ))}
      </div>

      <div className="flex justify-between font-black text-sm border-t border-gray-200 pt-3">
        <span className="text-gray-700">Subtotal</span>
        <span className="text-[#E87722]">{orden.subtotal}</span>
      </div>
    </Modal>
  );
}
