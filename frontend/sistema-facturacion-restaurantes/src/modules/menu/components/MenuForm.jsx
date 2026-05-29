import { useState } from "react";
import Swal   from "sweetalert2";
import Input  from "../../../global/components/Input.jsx";
import Button from "../../../global/components/Button.jsx";
import { createMenu, updateMenu } from "../menuService.js";

export default function MenuForm({ onCancel, onSuccess, isEdit = false, initialData = null }) {
  const [name,    setName]    = useState(initialData?.name ?? "");
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      if (isEdit) {
        await updateMenu(initialData.id, { name });
        await Swal.fire({
          icon: "success",
          title: "Menú actualizado",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        await createMenu({ name });
        await Swal.fire({
          icon: "success",
          title: "Menú creado",
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
        "Error al guardar el menú."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label="Nombre del Menú"
        placeholder="Ej: Almuerzos"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />

      {error && (
        <p className="text-xs text-red-500 -mt-2 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? (isEdit ? "Guardando..." : "Creando...")
            : (isEdit ? "Guardar cambios" : "Crear menú")}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
