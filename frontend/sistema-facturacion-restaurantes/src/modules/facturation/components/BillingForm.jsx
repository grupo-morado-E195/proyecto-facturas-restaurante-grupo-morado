import Button from "../../../global/components/Button.jsx";

const SAMPLE_ITEMS = [
  { nombre: "Bandeja Paisa", cantidad: 1, precioUnitario: 38000 },
  { nombre: "Ajiaco",        cantidad: 1, precioUnitario: 32000 },
  { nombre: "Jugo de Lulo",  cantidad: 2, precioUnitario: 8000  },
];

const TAX_RATE = 0.08;

function fmt(n) {
  return `$${n.toLocaleString("es-CO")}`;
}

export default function BillingForm({
  orderId = "#042",
  mesa = 3,
  mesero = "Laura Martínez",
  onCancel,
}) {
  const subtotal = SAMPLE_ITEMS.reduce((s, i) => s + i.cantidad * i.precioUnitario, 0);
  const impuesto = Math.round(subtotal * TAX_RATE);
  const total    = subtotal + impuesto;

  return (
    <div>
      <div className="bg-gray-50 rounded-lg px-4 py-3 mb-4 text-sm">
        <p className="font-bold text-gray-800">Orden {orderId}</p>
        <p className="text-gray-500 text-xs mt-0.5">Mesa {mesa} · Mesero: {mesero}</p>
      </div>

      <div className="mb-4">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Detalle de la orden
        </p>
        {SAMPLE_ITEMS.map((item) => (
          <div
            key={item.nombre}
            className="flex justify-between items-center py-2 border-b border-gray-100 last:border-0 text-sm"
          >
            <span className="text-gray-700">{item.cantidad}x {item.nombre}</span>
            <span className="font-semibold text-gray-800">
              {fmt(item.cantidad * item.precioUnitario)}
            </span>
          </div>
        ))}
      </div>

      <div className="border-t-2 border-gray-200 pt-3 space-y-1.5 mb-5">
        <div className="flex justify-between text-sm text-gray-600">
          <span>Subtotal</span>
          <span>{fmt(subtotal)}</span>
        </div>
        <div className="flex justify-between text-sm text-gray-600">
          <span>Impuesto al consumo (8%)</span>
          <span>{fmt(impuesto)}</span>
        </div>
        <div className="flex justify-between text-base font-black text-[#E87722] mt-1 pt-2 border-t border-gray-100">
          <span>Total</span>
          <span>{fmt(total)}</span>
        </div>
      </div>

      <div className="mb-5">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Método de pago
        </p>
        <div className="grid grid-cols-3 gap-2">
          {["Efectivo", "Tarjeta", "Transferencia"].map((m) => (
            <label
              key={m}
              className="flex items-center gap-2 border border-gray-200 rounded-lg px-3 py-2.5
              cursor-pointer hover:border-[#E87722] hover:bg-orange-50 transition-colors"
            >
              <input
                type="radio"
                name="metodoPago"
                value={m.toLowerCase()}
                defaultChecked={m === "Efectivo"}
                className="accent-[#E87722]"
              />
              <span className="text-sm text-gray-700">{m}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="flex gap-3">
        <Button type="submit" fullWidth>Confirmar facturación</Button>
        <Button variant="ghost" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </div>
  );
}
