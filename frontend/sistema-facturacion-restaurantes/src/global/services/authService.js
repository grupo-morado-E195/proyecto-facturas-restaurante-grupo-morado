import apiClient from "./apiClient.js";
import { jwtDecode } from "jwt-decode";

/**
 * Extrae los datos del usuario a partir del JWT decodificado.
 * Claims del JWT: { userId, rol, sub (email), tokenVersion }
 */
function extractUserFromToken(token) {
  const decoded = jwtDecode(token);
  return {
    id:       decoded.userId,
    email:    decoded.sub,
    rol:      decoded.rol?.replace("ROLE_", "").toLowerCase(),      // admin | mesero | chef | cajero
    nombre:   decoded.nombre ?? decoded.sub,
    apellido: decoded.apellido ?? "",
    name:     decoded.nombre ?? decoded.sub,
    lastname: decoded.apellido ?? "",
  };
}

/**
 * Inicia sesión contra el backend.
 * Retorna { user, token, requiresPasswordChange }
 */
export async function login(email, password) {
  const { data } = await apiClient.post("auth/login", { email, password });
  const { token, requiresPasswordChange } = data;

  // Decodifica el JWT para extraer userId, rol, email
  const user = extractUserFromToken(token);

  // Guarda en localStorage bajo las claves estándar del backlog
  localStorage.setItem("token", token);
  localStorage.setItem("role",  user.rol);

  return { user, token, requiresPasswordChange };
}

/**
 * Cierra sesión contra el backend (invalida el token en servidor).
 */
export async function logout() {
  try {
    await apiClient.post("auth/logout");
  } catch {
    // Si falla el logout remoto, limpiamos de todas formas
  } finally {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("sfr_user");
    localStorage.removeItem("sfr_token");
  }
}

/**
 * Recuperación de contraseña: solicita envío de contraseña temporal por correo.
 */
export async function requestPasswordReset(email) {
  const { data } = await apiClient.post("auth/reset-password", { email });
  return data;
}

/**
 * Cambia la contraseña del usuario autenticado.
 * @param {string} currentPassword
 * @param {string} newPassword
 * @param {string} confirmPassword
 */
export async function changePassword(currentPassword, newPassword, confirmPassword) {
  const { data } = await apiClient.post("auth/change-password", {
    currentPassword,
    newPassword,
    confirmPassword,
  });
  return data;
}

/**
 * Actualiza la contraseña temporal por una nueva (flujo reset-password).
 * @param {string} newPassword
 */
export async function updatePassword(newPassword) {
  const { data } = await apiClient.post("auth/update-password", { newPassword });
  return data;
}
