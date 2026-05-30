import Modal from "../../../global/components/Modal.jsx";
import Badge from "../../../global/components/Badge.jsx";

const ESTADO_BADGE = {
  PENDIENTE:      <Badge variant="danger">Pendiente</Badge>,
  EN_PREPARACION: <Badge variant="warning">En preparación</Badge>,
  LISTO:          <Badge variant="success">Listo</Badge>,
  LISTA:          <Badge variant="success">Listo</Badge>,
  CANCELADO:      <Badge variant="danger">Cancelado</Badge>,
  CANCELADA:      <Badge variant="danger">Cancelado</Badge>,
  PAGADO:         <Badge variant="info">Facturado</Badge>,
  FACTURADA:      <Badge variant="info">Facturado</Badge>,
};

const fmt = (amount) =>
  amount !== undefined
    ? `$${Number(amount).toLocaleString("es-CO")}`
    : "-";

export default function OrderView({ orden, onClose }) {
  if (!orden) return null;

  // Soporta tanto el formato del summary (id, numeroMesa, estado) como el detalle completo (id, tableNumber, details, status, subtotal, etc.)
  const ordenId     = orden.id;
  const mesaNum     = orden.tableNumber ?? orden.numeroMesa;
  const estado      = orden.status ?? orden.estado;
  const detalles    = orden.details ?? [];
  const subtotal    = orden.subtotal;
  const impuesto    = orden.consumptionTax;
  const total       = orden.total;

  // Buscar si hay alguna observación general o notas en los platos
  const notasGenerales = detalles.find((d) => d.observaciones)?.observaciones;

  return (
    <Modal title={`Detalle Orden #${String(ordenId).padStart(3, "0")}`} onClose={onClose}>
      <div className="bg-gray-50 rounded-lg px-4 py-3 mb-4 text-sm flex justify-between items-center">
        <div>
          <p className="font-bold text-gray-800">Mesa {mesaNum}</p>
          <p className="text-gray-500 text-xs mt-0.5">
            {detalles.length} plato{detalles.length !== 1 ? "s" : ""}
          </p>
        </div>
        {ESTADO_BADGE[estado] ?? <Badge>{estado}</Badge>}
      </div>

      {notasGenerales && (
        <div className="bg-amber-50 border-l-4 border-amber-500 p-4 mb-4 rounded-r-lg shadow-sm">
          <p className="text-xs font-black text-amber-800 uppercase tracking-wider flex items-center gap-1">
            ⚠️ OBSERVACIONES PARA LA COCINA:
          </p>
          <p className="text-base font-black text-amber-900 mt-1 break-words">
            {notasGenerales}
          </p>
        </div>
      )}

      <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
        Platos de la orden
      </p>
      <div className="border border-gray-200 rounded-lg overflow-hidden mb-4">
        <div className="grid grid-cols-[1fr_auto_80px] gap-2 px-3 py-2 bg-gray-50 border-b border-gray-200">
          <span className="text-xs font-bold text-gray-500">Plato</span>
          <span className="text-xs font-bold text-gray-500">Cant.</span>
          <span className="text-xs font-bold text-gray-500 text-right">Total</span>
        </div>
        {detalles.length === 0 ? (
          <p className="text-center text-gray-400 py-4 text-xs px-3">No hay detalles disponibles.</p>
        ) : (
          detalles.map((d, i) => (
            <div
              key={i}
              className="grid grid-cols-[1fr_auto_80px] gap-2 items-center px-3 py-2.5 border-b border-gray-100 last:border-0"
            >
              <div>
                <span className="text-sm text-gray-800">{d.nombrePlato}</span>
                {d.observaciones && (
                  <p className="text-xs text-gray-400 mt-0.5">{d.observaciones}</p>
                )}
              </div>
              <span className="text-xs text-gray-500 text-center">{d.cantidad}</span>
              <span className="text-sm font-semibold text-gray-800 text-right">
                {fmt(d.subtotalDetalle)}
              </span>
            </div>
          ))
        )}
      </div>

      {subtotal !== undefined && (
        <div className="flex justify-between font-black text-sm border-t border-gray-200 pt-3 flex-col gap-1">
          <div className="flex justify-between text-sm text-gray-600 font-normal">
            <span>Subtotal</span>
            <span>{fmt(subtotal)}</span>
          </div>
          {impuesto !== undefined && (
            <div className="flex justify-between text-sm text-gray-600 font-normal">
              <span>Impuesto al consumo (8%)</span>
              <span>{fmt(impuesto)}</span>
            </div>
          )}
          <div className="flex justify-between text-[#E87722] font-black border-t border-gray-100 pt-2 mt-1">
            <span>Total</span>
            <span>{fmt(total)}</span>
          </div>
        </div>
      )}
    </Modal>
  );
}
