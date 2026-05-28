import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";

const ROL_OPTIONS = [
  { value: "",       label: "Seleccionar rol..." },
  { value: "admin",  label: "Administrador"      },
  { value: "mesero", label: "Mesero"             },
  { value: "chef",   label: "Chef"               },
  { value: "cajero", label: "Cajero"             },
];

const ESTADO_OPTIONS = [
  { value: "activo",   label: "Activo"   },
  { value: "inactivo", label: "Inactivo" },
];

export default function UserForm({ onCancel, isEdit = false }) {
  return (
    <form onSubmit={(e) => e.preventDefault()} className="space-y-0">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4">
        <Input label="Nombre"    placeholder="Ej: Juan"         required />
        <Input label="Apellidos" placeholder="Ej: García López" required />
      </div>
      <Input label="Correo electrónico" type="email" placeholder="correo@restaurante.com" required />
      {!isEdit && (
        <Input label="Contraseña" type="password" placeholder="Mínimo 8 caracteres" required />
      )}
      <Select label="Rol" options={ROL_OPTIONS} required />
      {isEdit && <Select label="Estado" options={ESTADO_OPTIONS} />}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth>
          {isEdit ? "Guardar cambios" : "Registrar usuario"}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </form>
  );
}
