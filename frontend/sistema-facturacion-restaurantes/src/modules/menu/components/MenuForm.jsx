import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";

const ESTADO_OPTIONS = [
  { value: "activo",   label: "Activo"   },
  { value: "inactivo", label: "Inactivo" },
];

export default function MenuForm({ onCancel, isEdit = false }) {
  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <Input label="Nombre del Menú" placeholder="Ej: Almuerzos" required />
      <Select label="Estado" options={ESTADO_OPTIONS} />

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth>
          {isEdit ? "Guardar cambios" : "Crear menú"}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </form>
  );
}
