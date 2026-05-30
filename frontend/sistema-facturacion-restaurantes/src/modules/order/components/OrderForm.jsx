import { useState, useEffect } from "react";
import Swal   from "sweetalert2";
import Select from "../../../global/components/Select.jsx";
import Input  from "../../../global/components/Input.jsx";
import Button from "../../../global/components/Button.jsx";
import { getTables }                    from "../../table/tableService.js";
import { getAvailableDishes }           from "../../dish/dishService.js";
import { createOrder, updateOrder, getOrderById } from "../orderService.js";

export default function OrderForm({ onCancel, onSuccess, isEdit = false, ordenId = null }) {
  const [mesas,     setMesas]     = useState([]);
  const [platos,    setPlatos]    = useState([]);
  const [mesaId,    setMesaId]    = useState("");
  const [items,     setItems]     = useState({}); // { platoId: { cantidad, observaciones } }
  const [notas,     setNotas]     = useState("");
  const [loading,   setLoading]   = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [error,     setError]     = useState(null);

  useEffect(() => {
    async function loadData() {
      setLoadingData(true);
      try {
        const [mesasData, platosData] = await Promise.all([
          getTables(0, 100),
          getAvailableDishes(),
        ]);
        // Solo mesas ACTIVAS y DISPONIBLES para nueva orden
        const mesasDisponibles = (mesasData.content ?? []).filter(
          (m) => m.status === "ACTIVO" && m.disponibility === "DISPONIBLE"
        );
        setMesas(mesasDisponibles);
        setPlatos(platosData);

        // Si es edición, carga la orden existente
        if (isEdit && ordenId) {
          const orden = await getOrderById(ordenId);
          setMesaId(String(orden.tableNumber ?? ""));
          const itemsMap = {};
          let orderNotes = "";
          orden.details?.forEach((d) => {
            // Busca el plato por nombre en la lista de disponibles
            const platoMatch = platosData.find((p) => p.name === d.nombrePlato);
            if (platoMatch) {
              itemsMap[platoMatch.id] = {
                cantidad:     d.cantidad,
                observaciones: d.observaciones ?? "",
              };
              if (!orderNotes && d.observaciones) {
                orderNotes = d.observaciones;
              }
            }
          });
          setItems(itemsMap);
          setNotas(orderNotes);
        }
      } catch {
        setError("Error al cargar datos. Intenta de nuevo.");
      } finally {
        setLoadingData(false);
      }
    }
    loadData();
  }, [isEdit, ordenId]);

  const mesaOptions = [
    { value: "", label: "Seleccionar mesa..." },
    ...mesas.map((m) => ({ value: String(m.id), label: `Mesa ${m.number} (Libre)` })),
  ];

  const handleCantidad = (platoId, value) => {
    setItems((prev) => ({
      ...prev,
      [platoId]: { ...(prev[platoId] ?? { observaciones: "" }), cantidad: Math.max(0, Number(value)) },
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!mesaId) { setError("Selecciona una mesa."); return; }

    const detalles = platos
      .filter((p) => (items[p.id]?.cantidad ?? 0) > 0)
      .map((p) => ({
        platoId:      p.id,
        cantidad:     items[p.id].cantidad,
        observaciones: items[p.id]?.observaciones || notas,
      }));

    if (detalles.length === 0) {
      setError("Agrega al menos un plato a la orden.");
      return;
    }

    setLoading(true);
    try {
      if (isEdit) {
        await updateOrder(ordenId, { mesaId: Number(mesaId), detalles });
        await Swal.fire({
          icon: "success",
          title: "Orden actualizada",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        await createOrder({ mesaId: Number(mesaId), detalles });
        await Swal.fire({
          icon: "success",
          title: "Orden registrada",
          timer: 1500,
          showConfirmButton: false,
        });
      }
      onSuccess?.();
    } catch (err) {
      setError(
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Error al guardar la orden."
      );
    } finally {
      setLoading(false);
    }
  };

  const fmt = (price) =>
    price !== undefined ? `$${Number(price).toLocaleString("es-CO")}` : "";

  if (loadingData) {
    return <p className="text-center text-gray-400 py-8 text-sm">Cargando datos...</p>;
  }

  return (
    <form onSubmit={handleSubmit}>
      <Select
        label="Mesa"
        options={mesaOptions}
        value={mesaId}
        onChange={(e) => setMesaId(e.target.value)}
        required
      />

      <div className="mb-4">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Platos <span className="text-red-500">*</span>
        </p>
        <div className="border border-gray-200 rounded-lg overflow-hidden">
          <div className="grid grid-cols-[1fr_auto_80px] gap-2 px-3 py-2 bg-gray-50 border-b border-gray-200">
            <span className="text-xs font-bold text-gray-500">Plato</span>
            <span className="text-xs font-bold text-gray-500">Precio</span>
            <span className="text-xs font-bold text-gray-500">Cant.</span>
          </div>
          {platos.length === 0 ? (
            <p className="text-center text-gray-400 py-4 text-xs">No hay platos disponibles.</p>
          ) : (
            platos.map((plato) => (
              <div
                key={plato.id}
                className="grid grid-cols-[1fr_auto_80px] gap-2 items-center px-3 py-2.5 border-b border-gray-100 last:border-0"
              >
                <span className="text-sm text-gray-800 truncate">{plato.name}</span>
                <span className="text-xs text-gray-500 whitespace-nowrap">{fmt(plato.price)}</span>
                <input
                  type="number"
                  value={items[plato.id]?.cantidad ?? 0}
                  min={0}
                  max={plato.stock}
                  onChange={(e) => handleCantidad(plato.id, e.target.value)}
                  className="w-full px-2 py-1 border border-gray-200 rounded text-sm text-center
                    focus:outline-none focus:border-[#E87722]"
                />
              </div>
            ))
          )}
        </div>
      </div>

      <Input
        label="Observaciones"
        placeholder="Notas especiales para la cocina..."
        asTextarea
        rows={2}
        value={notas}
        onChange={(e) => setNotas(e.target.value)}
      />

      {error && (
        <p className="text-xs text-red-500 -mt-2 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? (isEdit ? "Guardando..." : "Registrando...")
            : (isEdit ? "Guardar cambios" : "Registrar orden")}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
