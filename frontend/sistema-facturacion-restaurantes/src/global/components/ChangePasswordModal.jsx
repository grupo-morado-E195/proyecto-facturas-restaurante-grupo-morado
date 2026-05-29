import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { changePassword } from "../services/authService.js";
import { ROUTES } from "../constants/routes.js";
import Modal  from "./Modal.jsx";
import Input  from "./Input.jsx";
import Button from "./Button.jsx";
import Swal   from "sweetalert2";

export default function ChangePasswordModal({ onClose }) {
  const { logout } = useAuth();
  const navigate   = useNavigate();

  const [current,  setCurrent]  = useState("");
  const [next,     setNext]     = useState("");
  const [confirm,  setConfirm]  = useState("");
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (next !== confirm) {
      setError("Las contraseñas nuevas no coinciden.");
      return;
    }

    setLoading(true);
    try {
      const data = await changePassword(current, next, confirm);
      // El backend invalida el token actual tras el cambio de contraseña.
      // El frontend debe descartar el token y redirigir al login.
      await Swal.fire({
        icon:             "success",
        title:            "¡Contraseña cambiada!",
        text:             data.message ?? "Contraseña actualizada correctamente. Por seguridad, deberás iniciar sesión nuevamente.",
        confirmButtonColor: "#E87722",
        confirmButtonText:  "Iniciar sesión",
      });
      await logout();
      navigate(ROUTES.LOGIN, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Error al cambiar la contraseña."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title="Cambiar Contraseña" onClose={onClose} size="sm">
      <form onSubmit={handleSubmit} className="space-y-0">
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

        {error && (
          <p className="text-xs text-red-500 mb-3 px-0.5">
            {error}
          </p>
        )}

        <div className="flex gap-3 pt-2">
          <Button type="submit" fullWidth disabled={loading}>
            {loading ? "Guardando..." : "Guardar cambios"}
          </Button>
          <Button variant="secondary" fullWidth onClick={onClose} disabled={loading}>
            Cancelar
          </Button>
        </div>
      </form>
    </Modal>
  );
}
