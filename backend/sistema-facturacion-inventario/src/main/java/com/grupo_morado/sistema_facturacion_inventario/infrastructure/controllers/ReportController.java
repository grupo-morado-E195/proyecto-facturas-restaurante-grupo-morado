package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.ReportUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador REST para el módulo de Informes.
 * Expone endpoints para generar PDFs de ventas diarias (ADMINISTRADOR)
 * y de cierre de caja (CAJERO).
 */
@RestController
@RequestMapping("/api/informes")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportUseCase reportUseCase;

    /**
     * Genera el informe de ventas diario en PDF para la fecha indicada.
     * Endpoint restringido a usuarios con rol ADMINISTRADOR.
     *
     * @param fecha          Fecha de filtrado en formato YYYY-MM-DD (parámetro de consulta).
     * @param userDetails    Detalles del usuario autenticado (extraído automáticamente del JWT).
     * @return ResponseEntity con el PDF como array de bytes y headers apropiados.
     */
    @GetMapping("/ventas/diario")
    public ResponseEntity<byte[]> getDailySalesReport(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("REST request para generar informe de ventas del día: {} por: {}",
                fecha, userDetails.getUsername());

        byte[] pdfBytes = reportUseCase.generateDailySalesReportPdf(fecha, userDetails.getUsername());

        String filename = "informe-ventas-" + fecha + ".pdf";
        return buildPdfResponse(pdfBytes, filename);
    }

    /**
     * Genera el informe de cierre de caja en PDF para la fecha actual.
     * Endpoint restringido a usuarios con rol CAJERO.
     *
     * @param userDetails Detalles del usuario autenticado (extraído automáticamente del JWT).
     * @return ResponseEntity con el PDF como array de bytes y headers apropiados.
     */
    @PostMapping("/caja/cierre")
    public ResponseEntity<byte[]> getCashClosureReport(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("REST request para generar informe de cierre de caja por: {}",
                userDetails.getUsername());

        byte[] pdfBytes = reportUseCase.generateCashClosureReportPdf(userDetails.getUsername());

        String filename = "cierre-caja-" + LocalDate.now() + ".pdf";
        return buildPdfResponse(pdfBytes, filename);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    /**
     * Construye la ResponseEntity con los headers correctos para descarga de PDF.
     *
     * @param pdfBytes Contenido del PDF.
     * @param filename Nombre del archivo para el header Content-Disposition.
     * @return ResponseEntity configurada para descarga de PDF.
     */
    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
