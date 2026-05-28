import Select from "../../../global/components/Select.jsx";
import Input  from "../../../global/components/Input.jsx";
import Button from "../../../global/components/Button.jsx";

const MESA_OPTIONS = [
  { value: "",  label: "Seleccionar mesa..." },
  { value: "2", label: "Mesa 2 (Libre)"      },
  { value: "4", label: "Mesa 4 (Libre)"      },
  { value: "6", label: "Mesa 6 (Libre)"      },
  { value: "8", label: "Mesa 8 (Libre)"      },
];

const SAMPLE_ITEMS = [
  { nombre: "Bandeja Paisa",   precio: "$38.000" },
  { nombre: "Ajiaco",          precio: "$32.000" },
  { nombre: "Jugo de Lulo",    precio: "$8.000"  },
  { nombre: "Arepa de Choclo", precio: "$12.000" },
];

export default function OrderForm({ onCancel }) {
  return (
    <form onSubmit={(e) => e.preventDefault()}>
      <Select label="Mesa" options={MESA_OPTIONS} required />

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
          {SAMPLE_ITEMS.map((item) => (
            <div
              key={item.nombre}
              className="grid grid-cols-[1fr_auto_80px] gap-2 items-center px-3 py-2.5 border-b border-gray-100 last:border-0"
            >
              <span className="text-sm text-gray-800 truncate">{item.nombre}</span>
              <span className="text-xs text-gray-500 whitespace-nowrap">{item.precio}</span>
              <input
                type="number"
                defaultValue={0}
                min={0}
                className="w-full px-2 py-1 border border-gray-200 rounded text-sm text-center
                  focus:outline-none focus:border-[#E87722]"
              />
            </div>
          ))}
        </div>
        <button
          type="button"
          className="mt-2 text-xs text-[#E87722] font-medium hover:underline"
        >
          + Agregar plato
        </button>
      </div>

      <Input
        label="Observaciones"
        placeholder="Notas especiales para la cocina..."
        asTextarea
        rows={2}
      />

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth>Registrar orden</Button>
        <Button variant="secondary" fullWidth onClick={onCancel}>Cancelar</Button>
      </div>
    </form>
  );
}
