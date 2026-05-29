import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Órdenes.
 * Endpoint base: /api/ordenes
 */

/**
 * Lista órdenes con paginación y filtro opcional por estado.
 * @param {string|null} status  Estado de la orden (PENDIENTE, EN_PREPARACION, LISTA, FACTURADA, CANCELADA)
 * @param {number} page
 * @param {number} size
 */
export async function getOrders({ status, page = 0, size = 50 } = {}) {
  const params = { page, size, sort: "createdAt,asc" };
  if (status) params.status = status;
  const { data } = await apiClient.get("ordenes", { params });
  return data; // Page<OrderSummaryResultDTO>
}

/** Obtiene el detalle de una orden por ID */
export async function getOrderById(id) {
  const { data } = await apiClient.get(`ordenes/${id}`);
  return data; // OrderResultDTO
}

/**
 * Crea una nueva orden (MESERO).
 * @param {{ mesaId: number, detalles: [{platoId, cantidad, observaciones}] }} payload
 */
export async function createOrder({ mesaId, detalles }) {
  const { data } = await apiClient.post("ordenes", { mesaId, detalles });
  return data; // OrderResultDTO
}

/**
 * Modifica una orden en estado PENDIENTE (MESERO).
 * @param {number} id
 * @param {{ mesaId: number, detalles: [{platoId, cantidad, observaciones}] }} payload
 */
export async function updateOrder(id, { mesaId, detalles }) {
  const { data } = await apiClient.put(`ordenes/${id}`, { mesaId, detalles });
  return data; // OrderResultDTO
}

/** Cancela una orden (MESERO / ADMIN) */
export async function cancelOrder(id) {
  const { data } = await apiClient.put(`ordenes/${id}/cancelar`);
  return data;
}

/**
 * Actualiza el estado de una orden (CHEF).
 * @param {number} id
 * @param {string} nuevoEstado  'EN_PREPARACION' | 'LISTA'
 */
export async function updateOrderStatus(id, nuevoEstado) {
  const { data } = await apiClient.put(`ordenes/${id}/estado`, { nuevoEstado });
  return data; // OrderResultDTO
}
