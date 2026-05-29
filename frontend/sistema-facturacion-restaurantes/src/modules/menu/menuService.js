import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Gestión de Menús.
 * Endpoint base: /api/menus
 */

/** Lista menús con paginación */
export async function getMenus(page = 0, size = 50) {
  const { data } = await apiClient.get("menus", {
    params: { page, size, sort: "id,asc" },
  });
  return data; // PageResultDTO<MenuResultDTO>
}

/** Crea un nuevo menú */
export async function createMenu({ name }) {
  const { data } = await apiClient.post("menus", { name });
  return data; // MenuResultDTO
}

/** Modifica el nombre de un menú */
export async function updateMenu(id, { name }) {
  const { data } = await apiClient.put(`menus/${id}`, { name });
  return data; // MenuResultDTO
}

/** Desactiva un menú */
export async function deactivateMenu(id) {
  const { data } = await apiClient.put(`menus/${id}/desactivar`);
  return data;
}

/** Reactiva un menú */
export async function reactivateMenu(id) {
  const { data } = await apiClient.put(`menus/${id}/reactivar`);
  return data;
}
