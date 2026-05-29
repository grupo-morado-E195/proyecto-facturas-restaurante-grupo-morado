import { useState } from "react";
import Swal   from "sweetalert2";
import Button from "../../../global/components/Button.jsx";
import { invoiceOrder } from "../billingService.js";

const fmt = (n) =>
  n !== undefined ? `$${Number(n).toLocaleString("es-CO")}` : "-";

export default function BillingForm({
  ordenId,
  mesa,
  mesero,
  ordenDetalle,
  onCancel,
  onSuccess,
}) {
  const [metodoPago, setMetodoPago] = useState("efectivo");
  const [loading,    setLoading]    = useState(false);
  const [error,      setError]      = useState(null);

  const items    = ordenDetalle?.details ?? [];
  const subtotal = ordenDetalle?.subtotal;
  const impuesto = ordenDetalle?.consumptionTax;
  const total    = ordenDetalle?.total;

  const handleConfirm = async () => {
    setError(null);
    setLoading(true);
    try {
      await invoiceOrder(ordenId);
      await Swal.fire({
        icon:             "success",
        title:            "¡Facturación exitosa!",
        text:             `Orden #${String(ordenId).padStart(3, "0")} facturada correctamente.`,
        confirmButtonColor: "#E87722",
      });
      onSuccess?.();
    } catch (err) {
      setError(
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Error al facturar la orden."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="bg-gray-50 rounded-lg px-4 py-3 mb-4 text-sm">
        <p className="font-bold text-gray-800">Orden #{String(ordenId).padStart(3, "0")}</p>
        <p className="text-gray-500 text-xs mt-0.5">Mesa {mesa} · Mesero: {mesero}</p>
      </div>

      <div className="mb-4">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Detalle de la orden
        </p>
        {items.length === 0 ? (
          <p className="text-sm text-gray-400 text-center py-2">Cargando detalle...</p>
        ) : (
          items.map((item, i) => (
            <div
              key={i}
              className="flex justify-between items-center py-2 border-b border-gray-100 last:border-0 text-sm"
            >
              <span className="text-gray-700">{item.cantidad}x {item.nombrePlato}</span>
              <span className="font-semibold text-gray-800">
                {fmt(item.subtotalDetalle)}
              </span>
            </div>
          ))
        )}
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
                checked={metodoPago === m.toLowerCase()}
                onChange={(e) => setMetodoPago(e.target.value)}
                className="accent-[#E87722]"
              />
              <span className="text-sm text-gray-700">{m}</span>
            </label>
          ))}
        </div>
      </div>

      {error && (
        <p className="text-xs text-red-500 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3">
        <Button type="button" fullWidth onClick={handleConfirm} disabled={loading}>
          {loading ? "Procesando..." : "Confirmar facturación"}
        </Button>
        <Button variant="ghost" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </div>
  );
}
