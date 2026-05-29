import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../global/hooks/useAuth.js";
import { updatePassword } from "../../global/services/authService.js";
import { ROUTES } from "../../global/constants/routes.js";
import Input from "../../global/components/Input.jsx";
import Button from "../../global/components/Button.jsx";
import Swal from "sweetalert2";

export default function UpdatePassword() {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (newPassword !== confirmPassword) {
      setError("Las contraseñas no coinciden.");
      return;
    }

    setLoading(true);
    try {
      const data = await updatePassword(newPassword);

      await Swal.fire({
        icon: "success",
        title: "¡Contraseña actualizada!",
        text: data.message ?? "Contraseña actualizada correctamente. Por seguridad, deberás iniciar sesión nuevamente.",
        confirmButtonColor: "#E87722",
        confirmButtonText: "Iniciar sesión",
      });

      await logout();
      navigate(ROUTES.LOGIN, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Ocurrió un error al actualizar la contraseña."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Columna Izquierda - Branding e Información (Idéntico a Login) */}
      <div
        className="hidden lg:flex flex-col items-center justify-center flex-1
        bg-gradient-to-br from-[#1A0A00] to-[#3D1A00] p-12 text-center"
      >
        <div className="w-20 h-20 rounded-2xl bg-[#E87722] flex items-center justify-center mb-6 shadow-2xl">
          <svg viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" className="w-10 h-10">
            <path strokeLinecap="round" strokeLinejoin="round"
              d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
        </div>
        <h1 className="text-white font-black text-3xl leading-tight">
          Sistema de Facturación
          <br />
          para Restaurantes
        </h1>
        <p className="text-[#FFA94D] font-semibold mt-2 text-base">SFR</p>
        <p className="text-gray-400 mt-6 text-sm leading-relaxed max-w-xs">
          Gestión de órdenes, inventario,
          <br />
          facturación e informes en un solo lugar.
        </p>

        <div className="mt-10 space-y-3 text-left w-full max-w-xs">
          {[
            "Gestión de mesas y órdenes",
            "Control de inventario de platos",
            "Facturación e informes diarios",
            "Múltiples roles de usuario",
          ].map((f) => (
            <div key={f} className="flex items-center gap-3">
              <div className="w-5 h-5 rounded-full bg-[#E87722]/20 flex items-center justify-center flex-shrink-0">
                <svg viewBox="0 0 20 20" fill="#E87722" className="w-3 h-3">
                  <path fillRule="evenodd" clipRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 01.414 0z" />
                </svg>
              </div>
              <span className="text-gray-300 text-sm">{f}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Columna Derecha - Formulario de actualización */}
      <div className="flex-1 flex items-center justify-center p-6 bg-[#FFF8F0] lg:max-w-[480px]">
        <div className="w-full max-w-sm">
          {/* Logo visible en móvil */}
          <div className="flex items-center gap-3 mb-8 lg:hidden">
            <div className="w-10 h-10 rounded-xl bg-[#E87722] flex items-center justify-center">
              <svg viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" className="w-5 h-5">
                <path strokeLinecap="round" strokeLinejoin="round"
                  d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <span className="font-black text-[#1A0A00] text-lg">SFR Sistema</span>
          </div>

          <div className="mb-8">
            <h2 className="text-2xl font-black text-gray-900">Actualizar Contraseña</h2>
            <p className="text-gray-500 text-sm mt-1">
              Ingresa tu nueva contraseña para continuar
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Nueva Contraseña"
              type="password"
              placeholder="••••••••"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
            <Input
              label="Confirmar Contraseña"
              type="password"
              placeholder="••••••••"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />

            <div className="pt-2">
              <Button type="submit" fullWidth disabled={loading}>
                {loading ? "Actualizando..." : "Actualizar Contraseña"}
              </Button>
            </div>
          </form>

          {error && (
            <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg flex gap-2">
              <svg viewBox="0 0 24 24" fill="none" stroke="#D64035" strokeWidth="2"
                className="w-4 h-4 flex-shrink-0 mt-0.5">
                <path strokeLinecap="round" strokeLinejoin="round"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <p className="text-red-800 text-xs">
                <strong>Error:</strong> {error}
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
