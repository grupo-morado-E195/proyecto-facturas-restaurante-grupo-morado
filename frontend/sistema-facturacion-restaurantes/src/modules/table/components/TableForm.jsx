import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";

const ESTADO_OPTIONS = [
  { value: "activa",   label: "Activa"   },
  { value: "inactiva", label: "Inactiva" },
];

export default function TableForm({ onCancel, isEdit = false }) {
  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <Input
        label="Número de Mesa"
        type="number"
        placeholder="Ej: 5"
        required
      />
      <Select label="Estado de Registro" options={ESTADO_OPTIONS} />

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth>
          {isEdit ? "Guardar cambios" : "Crear mesa"}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </form>
  );
}
