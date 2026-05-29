package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import java.time.LocalDate;

/**
 * Puerto de entrada para el módulo de Informes.
 * Define las operaciones disponibles para generar informes en PDF.
 */
public interface ReportUseCase {

    /**
     * Genera el PDF del informe de ventas diarias para la fecha indicada.
     * Restringido a usuarios con rol ADMINISTRADOR.
     *
     * @param fecha          Fecha sobre la que se genera el informe.
     * @param emailGenerador Email del administrador que solicita el informe (extraído del JWT).
     * @return Array de bytes con el contenido del PDF generado.
     */
    byte[] generateDailySalesReportPdf(LocalDate fecha, String emailGenerador);

    /**
     * Genera el PDF del informe de cierre de caja para la fecha actual.
     * Restringido a usuarios con rol CAJERO.
     *
     * @param emailCajero Email del cajero que realiza el cierre (extraído del JWT).
     * @return Array de bytes con el contenido del PDF generado.
     */
    byte[] generateCashClosureReportPdf(String emailCajero);
}
