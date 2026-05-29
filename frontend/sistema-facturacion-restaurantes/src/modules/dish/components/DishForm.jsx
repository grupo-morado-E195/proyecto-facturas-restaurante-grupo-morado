import { useState, useEffect } from "react";
import Swal   from "sweetalert2";
import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";
import { getMenus }                  from "../../menu/menuService.js";
import { createDish, updateDish }    from "../dishService.js";

const ESTADO_OPTIONS = [
  { value: "ACTIVO",   label: "Activo"   },
  { value: "INACTIVO", label: "Inactivo" },
];

export default function DishForm({ onCancel, onSuccess, isEdit = false, initialData = null }) {
  const [name,        setName]        = useState(initialData?.name        ?? "");
  const [description, setDescription] = useState(initialData?.description ?? "");
  const [price,       setPrice]       = useState(initialData?.price       ?? "");
  const [stock,       setStock]       = useState(initialData?.stock       ?? "");
  const [menuId,      setMenuId]      = useState(initialData?.menuId?.toString() ?? "");
  const [status,      setStatus]      = useState(initialData?.status      ?? "ACTIVO");
  const [menus,       setMenus]       = useState([]);
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState(null);

  useEffect(() => {
    getMenus(0, 100)
      .then((data) => setMenus(data.content ?? []))
      .catch(() => setError("No se pudieron cargar los menús."));
  }, []);

  const menuOptions = [
    { value: "", label: "Seleccionar menú..." },
    ...menus
      .filter((m) => m.status === "ACTIVO")
      .map((m) => ({ value: String(m.id), label: m.name })),
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!menuId) { setError("Selecciona un menú."); return; }
    setLoading(true);
    try {
      if (isEdit) {
        await updateDish(initialData.id, {
          name, description, price, stock,
          menuId: Number(menuId),
          status,
        });
        await Swal.fire({
          icon: "success",
          title: "Plato actualizado",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        await createDish({ name, description, price, stock, menuId: Number(menuId) });
        await Swal.fire({
          icon: "success",
          title: "Plato registrado",
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
        "Error al guardar el plato."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label="Nombre del Plato"
        placeholder="Ej: Bandeja Paisa"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />
      <Input
        label="Descripción"
        placeholder="Descripción del plato..."
        asTextarea
        rows={2}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <div className="grid grid-cols-2 gap-x-4">
        <Input
          label="Precio"
          type="number"
          placeholder="0.00"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          required
        />
        <Input
          label="Stock"
          type="number"
          placeholder="0"
          value={stock}
          onChange={(e) => setStock(e.target.value)}
          required
        />
      </div>
      <Select
        label="Menú"
        options={menuOptions}
        value={menuId}
        onChange={(e) => setMenuId(e.target.value)}
        required
      />
      {isEdit && (
        <Select
          label="Estado"
          options={ESTADO_OPTIONS}
          value={status}
          onChange={(e) => setStatus(e.target.value)}
        />
      )}

      {error && (
        <p className="text-xs text-red-500 -mt-2 mb-3 px-0.5">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? (isEdit ? "Guardando..." : "Registrando...")
            : (isEdit ? "Guardar cambios" : "Registrar plato")}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
