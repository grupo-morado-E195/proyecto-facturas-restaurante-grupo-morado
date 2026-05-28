import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";

const MENU_OPTIONS = [
  { value: "",          label: "Seleccionar menú..." },
  { value: "desayunos", label: "Desayunos"           },
  { value: "almuerzos", label: "Almuerzos"           },
  { value: "cenas",     label: "Cenas"               },
  { value: "bebidas",   label: "Bebidas"             },
  { value: "postres",   label: "Postres"             },
];

const ESTADO_OPTIONS = [
  { value: "activo",   label: "Activo"   },
  { value: "inactivo", label: "Inactivo" },
];

export default function DishForm({ onCancel, isEdit = false }) {
  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <Input label="Nombre del Plato"  placeholder="Ej: Bandeja Paisa"       required />
      <Input label="Descripción"       placeholder="Descripción del plato..." asTextarea rows={2} />
      <div className="grid grid-cols-2 gap-x-4">
        <Input label="Precio" type="number" placeholder="0.00" required />
        <Input label="Stock"  type="number" placeholder="0"    required />
      </div>
      <Select label="Menú" options={MENU_OPTIONS} required />
      {isEdit && <Select label="Estado" options={ESTADO_OPTIONS} />}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth>
          {isEdit ? "Guardar cambios" : "Registrar plato"}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </form>
  );
}
