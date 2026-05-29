import { useState } from "react";
import Swal   from "sweetalert2";
import Input  from "../../../global/components/Input.jsx";
import Button from "../../../global/components/Button.jsx";
import { createTable, updateTable } from "../tableService.js";

export default function TableForm({ onCancel, onSuccess, isEdit = false, initialData = null }) {
  const [number,  setNumber]  = useState(initialData?.number?.toString() ?? "");
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!number || Number(number) < 1) {
      setError("Ingresa un número de mesa válido.");
      return;
    }
    setLoading(true);
    try {
      if (isEdit) {
        await updateTable(initialData.id, { number: Number(number) });
        await Swal.fire({
          icon: "success",
          title: "Mesa actualizada",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        await createTable({ number: Number(number) });
        await Swal.fire({
          icon: "success",
          title: "Mesa creada",
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
        "Error al guardar la mesa."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label="Número de Mesa"
        type="number"
        placeholder="Ej: 5"
        value={number}
        onChange={(e) => setNumber(e.target.value)}
        required
      />

      {error && (
        <p className="text-xs text-red-500 -mt-2 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? (isEdit ? "Guardando..." : "Creando...")
            : (isEdit ? "Guardar cambios" : "Crear mesa")}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
