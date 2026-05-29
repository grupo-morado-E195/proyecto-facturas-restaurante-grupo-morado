import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Gestión de Mesas.
 * Endpoint base: /api/mesas
 */

/** Lista mesas con paginación */
export async function getTables(page = 0, size = 50) {
  const { data } = await apiClient.get("mesas", {
    params: { page, size, sort: "number,asc" },
  });
  return data; // PageResultDTO<TableResultDTO>
}

/** Crea una nueva mesa */
export async function createTable({ number }) {
  const { data } = await apiClient.post("mesas", { number });
  return data; // TableResultDTO
}

/** Modifica el número de una mesa */
export async function updateTable(id, { number }) {
  const { data } = await apiClient.put(`mesas/${id}`, { number });
  return data; // TableResultDTO
}

/** Desactiva una mesa */
export async function deactivateTable(id) {
  const { data } = await apiClient.put(`mesas/${id}/desactivar`);
  return data;
}

/** Reactiva una mesa */
export async function reactivateTable(id) {
  const { data } = await apiClient.put(`mesas/${id}/reactivar`);
  return data;
}
