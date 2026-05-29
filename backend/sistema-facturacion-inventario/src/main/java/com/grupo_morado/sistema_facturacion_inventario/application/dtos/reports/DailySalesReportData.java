package com.grupo_morado.sistema_facturacion_inventario.application.dtos.reports;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos consolidados del informe de ventas diario.
 *
 * @param ventasTotales    Suma de todos los totales de órdenes PAGADAS en el día.
 * @param ventasPorMesero  Lista de ventas agrupadas por mesero.
 * @param platoMasVendido  Nombre del plato con mayor cantidad vendida.
 * @param platoMenosVendido Nombre del plato con menor cantidad vendida.
 */
public record DailySalesReportData(
        BigDecimal ventasTotales,
        List<MeseroSalesData> ventasPorMesero,
        String platoMasVendido,
        String platoMenosVendido
) {
}
