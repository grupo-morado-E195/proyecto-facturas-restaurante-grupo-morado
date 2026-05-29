import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../global/hooks/useAuth.js";
import { getDashboardByRole } from "../../global/constants/routes.js";
import { requestPasswordReset } from "../../global/services/authService.js";
import Input  from "../../global/components/Input.jsx";
import Button from "../../global/components/Button.jsx";

export default function Login() {
  const { login, loading, requiresPasswordChange } = useAuth();
  const navigate = useNavigate();

  const [showForgot, setShowForgot] = useState(false);
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState(null);

  // Estado para flujo de recuperación de contraseña
  const [forgotEmail,   setForgotEmail]   = useState("");
  const [forgotLoading, setForgotLoading] = useState(false);
  const [forgotSuccess, setForgotSuccess] = useState(null);
  const [forgotError,   setForgotError]   = useState(null);

  const handleSubmit = async (e) => {
  e.preventDefault();
  setError(null);
  try {
    const { user, requiresPasswordChange } = await login(email, password);
    if (requiresPasswordChange) {
      navigate("/update-password", { replace: true });
    } else {
      navigate(getDashboardByRole(user.rol), { replace: true });
    }
  } catch (err) {
    setError(err.message ?? "Credenciales incorrectas");
  }
};

  const handleForgot = async (e) => {
    e.preventDefault();
    setForgotLoading(true);
    setForgotError(null);
    setForgotSuccess(null);
    try {
      const data = await requestPasswordReset(forgotEmail);
      setForgotSuccess(data.message ?? "Se ha enviado una contraseña temporal a tu correo.");
    } catch (err) {
      setForgotError(
        err.response?.data?.message ??
        err.message ??
        "No se pudo enviar el correo. Verifica el email ingresado."
      );
    } finally {
      setForgotLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      <div
        className="hidden lg:flex flex-col items-center justify-center flex-1
        bg-gradient-to-br from-[#1A0A00] to-[#3D1A00] p-12 text-center"
      >
        <div className="w-32 h-32 flex items-center justify-center mb-6">
          <img src="/logo.png" alt="Restaurante Logo" className="w-full h-full object-contain" />
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
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" />
                </svg>
              </div>
              <span className="text-gray-300 text-sm">{f}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center p-6 bg-[#FFF8F0] lg:max-w-[480px]">
        <div className="w-full max-w-sm">
          <div className="flex items-center gap-3 mb-8 lg:hidden">
            <div className="w-14 h-14 flex items-center justify-center">
              <img src="/logo.png" alt="Restaurante Logo" className="w-full h-full object-contain" />
            </div>
            <span className="font-black text-[#1A0A00] text-lg">SFR Sistema</span>
          </div>

          {!showForgot ? (
            <>
              <div className="mb-8">
                <h2 className="text-2xl font-black text-gray-900">Iniciar Sesión</h2>
                <p className="text-gray-500 text-sm mt-1">
                  Ingresa tus credenciales para continuar
                </p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-0">
                <Input
                  label="Correo electrónico"
                  type="email"
                  placeholder="correo@restaurante.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
                <Input
                  label="Contraseña"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />

                <div className="flex justify-end mb-5">
                  <button
                    type="button"
                    onClick={() => setShowForgot(true)}
                    className="text-[#E87722] text-sm font-bold hover:text-[#C45F10] transition-colors"
                  >
                    ¿Olvidaste tu contraseña?
                  </button>
                </div>

                <Button type="submit" fullWidth disabled={loading}>
                  {loading ? "Verificando..." : "Ingresar"}
                </Button>
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
            </>
          ) : (
            <>
              <button
                onClick={() => { setShowForgot(false); setForgotSuccess(null); setForgotError(null); setForgotEmail(""); }}
                className="flex items-center gap-1.5 text-gray-500 text-sm mb-6 hover:text-gray-700 transition-colors"
              >
                <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="w-4 h-4">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                    d="M15 19l-7-7 7-7" />
                </svg>
                Volver al login
              </button>
              <div className="mb-8">
                <h2 className="text-2xl font-black text-gray-900">Recuperar Contraseña</h2>
                <p className="text-gray-500 text-sm mt-1">
                  Ingresa tu correo para recibir una contraseña temporal
                </p>
              </div>
              <form onSubmit={handleForgot}>
                <Input
                  label="Correo electrónico"
                  type="email"
                  placeholder="correo@restaurante.com"
                  value={forgotEmail}
                  onChange={(e) => setForgotEmail(e.target.value)}
                  required
                />
                <Button type="submit" fullWidth disabled={forgotLoading}>
                  {forgotLoading ? "Enviando..." : "Enviar contraseña temporal"}
                </Button>
              </form>
              {forgotSuccess && (
                <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                  <p className="text-green-800 text-xs">{forgotSuccess}</p>
                </div>
              )}
              {forgotError && (
                <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                  <p className="text-red-800 text-xs"><strong>Error:</strong> {forgotError}</p>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
