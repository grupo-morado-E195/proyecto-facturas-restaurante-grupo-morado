import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Informes.
 * Endpoint base: /api/informes
 */

/**
 * Genera el informe de ventas diario en PDF.
 * Restringido a ADMINISTRADOR.
 * @param {string} fecha  Fecha en formato 'YYYY-MM-DD'
 * @returns {Blob} PDF como Blob
 */
export async function getDailySalesReport(fecha) {
  const response = await apiClient.get("informes/ventas/diario", {
    params:       { fecha },
    responseType: "blob",
  });
  return response.data; // Blob
}

/**
 * Genera el informe de cierre de caja en PDF.
 * Restringido a CAJERO.
 * @returns {Blob} PDF como Blob
 */
export async function getCashClosureReport() {
  const response = await apiClient.post("informes/caja/cierre", null, {
    responseType: "blob",
  });
  return response.data; // Blob
}

/**
 * Descarga un Blob como archivo PDF.
 * @param {Blob}   blob      Contenido del PDF
 * @param {string} filename  Nombre del archivo a descargar
 */
export function downloadPdf(blob, filename) {
  const url  = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href     = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
