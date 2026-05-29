package com.grupo_morado.sistema_facturacion_inventario.application.dtos.reports;

import java.math.BigDecimal;

/**
 * Datos de ventas agrupados por mesero para el informe diario.
 *
 * @param nombreMesero Nombre completo del mesero.
 * @param totalVentas  Total facturado por el mesero en el día.
 */
public record MeseroSalesData(String nombreMesero, BigDecimal totalVentas) {
}
