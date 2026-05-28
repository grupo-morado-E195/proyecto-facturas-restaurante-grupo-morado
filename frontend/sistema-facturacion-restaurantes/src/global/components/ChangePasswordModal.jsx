import { useState } from "react";
import Modal  from "./Modal.jsx";
import Input  from "./Input.jsx";
import Button from "./Button.jsx";

export default function ChangePasswordModal({ onClose }) {
  const [current,  setCurrent]  = useState("");
  const [next,     setNext]     = useState("");
  const [confirm,  setConfirm]  = useState("");

  return (
    <Modal title="Cambiar Contraseña" onClose={onClose} size="sm">
      <form onSubmit={(e) => e.preventDefault()} className="space-y-0">
        <Input
          label="Contraseña actual"
          type="password"
          placeholder="••••••••"
          value={current}
          onChange={(e) => setCurrent(e.target.value)}
          required
        />
        <Input
          label="Nueva contraseña"
          type="password"
          placeholder="Mínimo 8 caracteres"
          value={next}
          onChange={(e) => setNext(e.target.value)}
          required
        />
        <Input
          label="Confirmar nueva contraseña"
          type="password"
          placeholder="Repite la nueva contraseña"
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          required
        />

        {next && confirm && next !== confirm && (
          <p className="text-xs text-red-500 -mt-3 mb-3 px-0.5">
            Las contraseñas no coinciden.
          </p>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" fullWidth>
            Guardar cambios
          </Button>
          <Button variant="secondary" fullWidth onClick={onClose}>
            Cancelar
          </Button>
        </div>
      </form>
    </Modal>
  );
}
