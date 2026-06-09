import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";
import { getTables } from "../../table/tableService.js";
import { getAvailableDishes } from "../../dish/dishService.js";
import { createOrder, updateOrder, getOrderById } from "../orderService.js";

export default function OrderForm({
  onCancel,
  onSuccess,
  isEdit = false,
  ordenId = null,
}) {
  const [mesas, setMesas] = useState([]);
  const [platos, setPlatos] = useState([]);
  const [mesaId, setMesaId] = useState("");
  const [detallesOrden, setDetallesOrden] = useState([]); // [{ platoId, nombrePlato, precioUnitario, cantidad, observaciones, stock }]
  const [selectedPlatoId, setSelectedPlatoId] = useState("");
  const [expandedObs, setExpandedObs] = useState({}); // { platoId: boolean }
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadData() {
      setLoadingData(true);
      try {
        const [mesasData, platosData] = await Promise.all([
          getTables(0, 100),
          getAvailableDishes(),
        ]);
        setPlatos(platosData);

        let activeTableNumber = null;
        let detailsList = [];

        // Si es edición, carga la orden existente
        if (isEdit && ordenId) {
          const orden = await getOrderById(ordenId);
          activeTableNumber = orden.tableNumber;

          orden.details?.forEach((d) => {
            // Busca el plato por ID o por nombre en la lista de disponibles
            const platoMatch = platosData.find(
              (p) => p.id === d.platoId || p.name === d.nombrePlato,
            );
            detailsList.push({
              platoId: d.platoId ?? platoMatch?.id,
              nombrePlato: d.nombrePlato,
              precioUnitario: d.precioUnitario,
              cantidad: d.cantidad,
              observaciones: d.observaciones ?? "",
              stock: platoMatch ? platoMatch.stock + d.cantidad : d.cantidad,
            });
          });
        }
        setDetallesOrden(detailsList);

        // Solo mesas ACTIVAS y (DISPONIBLES o la mesa actual de la orden en edición)
        const mesasFiltradas = (mesasData.content ?? []).filter(
          (m) =>
            m.status === "ACTIVO" &&
            (m.disponibility === "DISPONIBLE" ||
              (activeTableNumber !== null && m.number === activeTableNumber)),
        );
        setMesas(mesasFiltradas);

        // Si es edición, buscamos la mesa por su número para establecer el ID de mesa correcto en el select
        if (activeTableNumber !== null) {
          const mesaMatch = (mesasData.content ?? []).find(
            (m) => m.number === activeTableNumber,
          );
          if (mesaMatch) {
            setMesaId(String(mesaMatch.id));
          }
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
    ...mesas.map((m) => ({
      value: String(m.id),
      label: `Mesa ${m.number} (Libre)`,
    })),
  ];

  const handleAddDish = (platoId) => {
    const dish = platos.find((p) => p.id === Number(platoId));
    if (!dish) return;

    // Evitar duplicados
    if (detallesOrden.some((d) => d.platoId === dish.id)) return;

    setDetallesOrden((prev) => [
      ...prev,
      {
        platoId: dish.id,
        nombrePlato: dish.name,
        precioUnitario: dish.price,
        cantidad: 1,
        observaciones: "",
        stock: dish.stock,
      },
    ]);
  };

  const handleCantidad = (platoId, value) => {
    const qty = Math.max(1, Number(value));
    setDetallesOrden((prev) =>
      prev.map((item) => {
        if (item.platoId === platoId) {
          return { ...item, cantidad: Math.min(qty, item.stock) };
        }
        return item;
      }),
    );
  };

  const handleRemoveDish = (platoId) => {
    setDetallesOrden((prev) => prev.filter((item) => item.platoId !== platoId));
  };

  const handleObservacion = (platoId, obs) => {
    setDetallesOrden((prev) =>
      prev.map((item) => {
        if (item.platoId === platoId) {
          return { ...item, observaciones: obs.slice(0, 126) };
        }
        return item;
      }),
    );
  };

  const toggleObs = (platoId) => {
    setExpandedObs((prev) => ({
      ...prev,
      [platoId]: !prev[platoId],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!mesaId) {
      setError("Selecciona una mesa.");
      return;
    }

    const detalles = detallesOrden.map((d) => ({
      platoId: d.platoId,
      cantidad: d.cantidad,
      observaciones: d.observaciones.trim() || null,
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
          "Error al guardar la orden.",
      );
    } finally {
      setLoading(false);
    }
  };

  const fmt = (price) =>
    price !== undefined ? `$${Number(price).toLocaleString("es-CO")}` : "";

  // Cálculos de totales estimados en caliente
  const subtotalOrden = detallesOrden.reduce(
    (sum, item) => sum + item.cantidad * item.precioUnitario,
    0,
  );
  const impuestoOrden = subtotalOrden * 0.08;
  const totalOrden = subtotalOrden + impuestoOrden;

  // Filtrar platos que aún no han sido agregados
  const platosDisponiblesParaAgregar = platos.filter(
    (p) => !detallesOrden.some((d) => d.platoId === p.id),
  );

  if (loadingData) {
    return (
      <p className="text-center text-gray-400 py-8 text-sm">
        Cargando datos...
      </p>
    );
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

      {/* Selector de plato a agregar */}
      <div className="mb-5 bg-gray-50 border border-gray-200 rounded-lg p-3">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Agregar plato a la orden
        </p>
        <div className="flex gap-2">
          <select
            value={selectedPlatoId}
            onChange={(e) => setSelectedPlatoId(e.target.value)}
            className="flex-1 px-3 py-2 border border-gray-200 rounded-lg text-sm bg-white focus:outline-none focus:border-[#E87722] text-gray-700"
          >
            <option value="">Selecciona un plato...</option>
            {platosDisponiblesParaAgregar.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name} - {fmt(p.price)} (Stock: {p.stock})
              </option>
            ))}
          </select>
          <Button
            type="button"
            onClick={() => {
              if (selectedPlatoId) {
                handleAddDish(selectedPlatoId);
                setSelectedPlatoId("");
              }
            }}
            disabled={!selectedPlatoId}
          >
            Agregar
          </Button>
        </div>
      </div>

      {/* Lista de platos agregados */}
      <div className="mb-5">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide mb-2">
          Detalles de la Orden <span className="text-red-500">*</span>
        </p>
        <div className="border border-gray-200 rounded-lg p-3 bg-white space-y-3 max-h-[300px] overflow-y-auto">
          {detallesOrden.length === 0 ? (
            <p className="text-center text-gray-400 py-6 text-xs">
              No has agregado platos a la orden todavía.
            </p>
          ) : (
            detallesOrden.map((item) => {
              const subtotalPlato = item.cantidad * item.precioUnitario;
              return (
                <div
                  key={item.platoId}
                  className="border-b border-gray-100 pb-3 last:border-0 last:pb-0"
                >
                  <div className="flex items-center justify-between gap-3">
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-semibold text-gray-800 truncate">
                        {item.nombrePlato}
                      </p>
                      <p className="text-xs text-gray-500">
                        {fmt(item.precioUnitario)} c/u
                      </p>
                    </div>

                    <div className="flex items-center gap-3 shrink-0">
                      {/* Selector de cantidad */}
                      <div className="flex items-center border border-gray-200 rounded-lg overflow-hidden h-8 bg-white">
                        <button
                          type="button"
                          onClick={() =>
                            handleCantidad(item.platoId, item.cantidad - 1)
                          }
                          className="px-2.5 bg-gray-50 text-gray-600 hover:bg-gray-100 transition-colors h-full text-base font-bold focus:outline-none"
                        >
                          -
                        </button>
                        <input
                          type="number"
                          value={item.cantidad}
                          min={1}
                          max={item.stock}
                          onChange={(e) =>
                            handleCantidad(item.platoId, e.target.value)
                          }
                          className="w-10 text-center text-sm border-0 focus:outline-none focus:ring-0 p-0 font-medium text-gray-800"
                        />
                        <button
                          type="button"
                          onClick={() =>
                            handleCantidad(item.platoId, item.cantidad + 1)
                          }
                          className="px-2.5 bg-gray-50 text-gray-600 hover:bg-gray-100 transition-colors h-full text-base font-bold focus:outline-none"
                        >
                          +
                        </button>
                      </div>

                      {/* Subtotal del plato */}
                      <span className="text-sm font-semibold text-gray-800 min-w-[70px] text-right">
                        {fmt(subtotalPlato)}
                      </span>

                      {/* Acciones */}
                      <div className="flex gap-1">
                        <button
                          type="button"
                          onClick={() => toggleObs(item.platoId)}
                          className={`p-1.5 rounded-lg border transition-colors focus:outline-none ${
                            item.observaciones
                              ? "bg-amber-50 border-amber-200 text-amber-600 hover:bg-amber-100"
                              : "bg-gray-50 border-gray-200 text-gray-400 hover:text-gray-600 hover:bg-gray-100"
                          }`}
                          title="Observación de plato"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                            strokeWidth={1.5}
                            stroke="currentColor"
                            className="w-4 h-4"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 0 1 .865-.501 48.172 48.172 0 0 0 3.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0 0 12 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018Z"
                            />
                          </svg>
                        </button>
                        <button
                          type="button"
                          onClick={() => handleRemoveDish(item.platoId)}
                          className="p-1.5 rounded-lg border border-red-100 bg-red-50 text-red-500 hover:bg-red-100 transition-colors focus:outline-none"
                          title="Eliminar plato"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                            strokeWidth={1.5}
                            stroke="currentColor"
                            className="w-4 h-4"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="m14.74 9-.346 9m-4.788 0L9 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                            />
                          </svg>
                        </button>
                      </div>
                    </div>
                  </div>
                  {/* Indicador visual de observación */}
                  {item.observaciones && (
                    <div className="flex items-center gap-1.5 mt-1 text-[11px] text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-100 w-fit">
                      <span className="font-bold">📝 Nota:</span>
                      <span className="max-w-[300px] break-words">
                        {item.observaciones}
                      </span>
                    </div>
                  )}
                  {/* Input de observación para el plato (inline y expandible) */}
                  {expandedObs[item.platoId] && (
                    <div className="mt-2.5 flex gap-2">
                      <input
                        type="text"
                        placeholder="Observaciones de este plato (ej. Sin cebolla, término medio)..."
                        value={item.observaciones}
                        onChange={(e) =>
                          handleObservacion(item.platoId, e.target.value)
                        }
                        maxLength={126}
                        className="flex-1 px-3 py-1.5 text-xs border border-amber-200 bg-amber-50/10 rounded-lg focus:outline-none focus:border-[#E87722] text-gray-700"
                        autoFocus
                      />
                      <button
                        type="button"
                        onClick={() => toggleObs(item.platoId)}
                        className="px-3 py-1 bg-gray-100 hover:bg-gray-200 text-gray-600 rounded-lg text-xs font-semibold focus:outline-none"
                      >
                        Aceptar
                      </button>
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>

      {/* Resumen de totales estimados */}
      {detallesOrden.length > 0 && (
        <div className="mb-5 bg-orange-50/40 border border-orange-100 rounded-lg p-3 text-xs space-y-1.5">
          <div className="flex justify-between text-gray-600">
            <span>Subtotal</span>
            <span>{fmt(subtotalOrden)}</span>
          </div>
          <div className="flex justify-between text-gray-600">
            <span>Impuesto al consumo (8%)</span>
            <span>{fmt(impuestoOrden)}</span>
          </div>
          <div className="flex justify-between font-bold text-[#E87722] text-sm pt-1.5 border-t border-orange-100">
            <span>Total estimado</span>
            <span>{fmt(totalOrden)}</span>
          </div>
        </div>
      )}

      {error && (
        <p className="text-xs text-red-500 -mt-2 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? isEdit
              ? "Guardando..."
              : "Registrando..."
            : isEdit
              ? "Guardar cambios"
              : "Registrar orden"}
        </Button>
        <Button
          variant="secondary"
          fullWidth
          onClick={onCancel}
          disabled={loading}
        >
          Cancelar
        </Button>
      </div>
    </form>
  );
}
