import { createContext, useCallback, useMemo, useState } from "react";
import { login as loginService, logout as logoutService } from "../services/authService.js";
import { jwtDecode } from "jwt-decode";

export const AuthContext = createContext(null);

/** Lee el usuario desde el token JWT almacenado en localStorage */
function loadUserFromStorage() {
  try {
    const token = localStorage.getItem("token");
    if (!token) return null;
    const decoded = jwtDecode(token);
    // Reconstruye el objeto user con los claims del JWT
    return {
      id:       decoded.userId,
      email:    decoded.sub,
      rol:      decoded.rol?.replace("ROLE_", "").toLowerCase(),
      nombre:   decoded.nombre ?? decoded.sub,
      apellido: decoded.apellido ?? "",
      name:     decoded.nombre ?? decoded.sub,
      lastname: decoded.apellido ?? "",
    };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user,    setUser]    = useState(loadUserFromStorage);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);
  const [requiresPasswordChange, setRequiresPasswordChange] = useState(false);

  const login = useCallback(async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const { user: userData, requiresPasswordChange: pwChange } =
        await loginService(email, password);
      // El token ya fue guardado en localStorage por loginService
      // Guardamos también el sfr_user para compatibilidad con código existente
      localStorage.setItem("sfr_user",  JSON.stringify(userData));
      localStorage.setItem("sfr_token", localStorage.getItem("token") ?? "");
      setUser(userData);
      setRequiresPasswordChange(!!pwChange);
      return { user: userData, requiresPasswordChange: !!pwChange };
    } catch (err) {
      const msg =
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Error al iniciar sesión";
      setError(msg);
      throw new Error(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await logoutService();
    } finally {
      setUser(null);
      setRequiresPasswordChange(false);
    }
  }, []);

  const value = useMemo(
    () => ({
      user,
      loading,
      error,
      login,
      logout,
      isAuthenticated: !!user,
      requiresPasswordChange,
    }),
    [user, loading, error, login, logout, requiresPasswordChange]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
