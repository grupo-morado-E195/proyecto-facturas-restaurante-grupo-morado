import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Gestión de Usuarios.
 * Endpoint base: /api/usuarios
 * Restringido a ADMINISTRADOR.
 */

/** Lista usuarios con paginación */
export async function getUsers(page = 0, size = 20) {
  const { data } = await apiClient.get("usuarios", {
    params: { page, size, sort: "id,asc" },
  });
  return data; // PageResultDTO<UserResultDTO>
}

/** Obtiene el detalle de un usuario por ID */
export async function getUserById(id) {
  const { data } = await apiClient.get(`usuarios/${id}`);
  return data; // UserResultDTO
}

/** Lista los roles disponibles (para el dropdown) */
export async function getRoles() {
  const { data } = await apiClient.get("usuarios/roles");
  return data; // [{ id, name }]
}

/** Registra un nuevo usuario */
export async function createUser({ name, lastname, email, roleId, password }) {
  const { data } = await apiClient.post("usuarios", { name, lastname, email, roleId, password });
  return data; // UserResultDTO
}

/** Modifica nombre, apellidos, rol y opcionalmente contraseña de un usuario */
export async function updateUser(id, { name, lastname, roleId, password }) {
  const { data } = await apiClient.put(`usuarios/${id}`, { name, lastname, roleId, password });
  return data; // UserResultDTO
}

/** Desactiva un usuario */
export async function deactivateUser(id) {
  const { data } = await apiClient.put(`usuarios/${id}/desactivar`);
  return data; // UserResultDTO
}

/** Reactiva un usuario */
export async function reactivateUser(id) {
  const { data } = await apiClient.put(`usuarios/${id}/reactivar`);
  return data; // UserResultDTO
}

/** Crea un nuevo rol */
export async function createRole(name) {
  const { data } = await apiClient.post("usuarios/roles", { name });
  return data; // { id, name }
}

/** Modifica un rol existente */
export async function updateRole(id, name) {
  const { data } = await apiClient.put(`usuarios/roles/${id}`, { name });
  return data; // { id, name }
}

/** Elimina un rol por ID */
export async function deleteRole(id) {
  const { data } = await apiClient.delete(`usuarios/roles/${id}`);
  return data; // { message }
}
