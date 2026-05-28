import { createContext, useCallback, useMemo, useState } from "react";
import { login as loginService, logout as logoutService } from "../services/authService.js";

export const AuthContext = createContext(null);

function loadUserFromStorage() {
  try {
    const raw = localStorage.getItem("sfr_user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function saveSession(user, token) {
  localStorage.setItem("sfr_user", JSON.stringify(user));
  localStorage.setItem("sfr_token", token);
}

function clearSession() {
  localStorage.removeItem("sfr_user");
  localStorage.removeItem("sfr_token");
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUserFromStorage);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const login = useCallback(async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const { user: userData, token } = await loginService(email, password);
      saveSession(userData, token);
      setUser(userData);
      return userData;
    } catch (err) {
      setError(err.message ?? "Error al iniciar sesión");
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await logoutService();
    } finally {
      clearSession();
      setUser(null);
    }
  }, []);

  const value = useMemo(
    () => ({ user, loading, error, login, logout, isAuthenticated: !!user }),
    [user, loading, error, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
