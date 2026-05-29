import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Facturación.
 * Endpoint base: /api/facturacion
 * Restringido a CAJERO.
 */

/**
 * Factura una orden existente (la pasa a estado FACTURADA).
 * @param {number} ordenId  ID de la orden a facturar
 * @param {string} metodoPago  Método de pago (opcional; el backend no lo requiere actualmente)
 */
export async function invoiceOrder(ordenId) {
  const { data } = await apiClient.put(`facturacion/ordenes/${ordenId}/facturar`);
  return data; // OrderResultDTO
}
