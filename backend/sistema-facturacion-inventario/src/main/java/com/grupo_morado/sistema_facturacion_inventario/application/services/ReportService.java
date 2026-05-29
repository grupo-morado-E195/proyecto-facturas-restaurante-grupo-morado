package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.reports.DailySalesReportData;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.reports.MeseroSalesData;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.ReportUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.ReportProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/**
 * Servicio que implementa la lógica de negocio para la generación de informes en PDF.
 * Cumple la restricción de arquitectura hexagonal: no accede a repositorios directamente,
 * solo a través de los puertos de salida definidos en la capa de aplicación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements ReportUseCase {

    // ─── Colores corporativos ──────────────────────────────────────────────────
    private static final DeviceRgb COLOR_PRIMARY     = new DeviceRgb(232, 119, 34);   // #E87722
    private static final DeviceRgb COLOR_DARK        = new DeviceRgb(26,  10,  0);    // #1A0A00
    private static final DeviceRgb COLOR_LIGHT       = new DeviceRgb(255, 248, 240);  // #FFF8F0
    private static final DeviceRgb COLOR_MUTED       = new DeviceRgb(120, 120, 120);  // gris medio

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("es", "CO"));

    private final ReportProviderPort reportProviderPort;
    private final UserProviderPort   userProviderPort;

    // ─── Implementación del puerto de entrada ──────────────────────────────────

    @Override
    public byte[] generateDailySalesReportPdf(LocalDate fecha, String emailGenerador) {
        log.info("Generando informe de ventas diarias para la fecha: {} solicitado por: {}", fecha, emailGenerador);

        User generador = resolveUser(emailGenerador);
        String nombreGenerador = generador.getName() + " " + generador.getLastname();

        DailySalesReportData data = collectReportData(fecha);
        return buildPdf(data, "INFORME DE VENTAS", fecha, nombreGenerador);
    }

    @Override
    public byte[] generateCashClosureReportPdf(String emailCajero) {
        LocalDate hoy = LocalDate.now();
        log.info("Generando informe de cierre de caja para la fecha: {} solicitado por: {}", hoy, emailCajero);

        User cajero = resolveUser(emailCajero);
        String nombreGenerador = cajero.getName() + " " + cajero.getLastname();

        DailySalesReportData data = collectReportData(hoy);
        return buildPdf(data, "INFORME DE CIERRE DE CAJA", hoy, nombreGenerador);
    }

    // ─── Métodos privados ──────────────────────────────────────────────────────

    /**
     * Recupera el usuario por email o lanza NotFoundException si no existe.
     */
    private User resolveUser(String email) {
        return userProviderPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        "El usuario con correo '" + email + "' no fue encontrado."));
    }

    /**
     * Consulta todos los datos necesarios para construir el informe de la fecha indicada.
     * Solo considera órdenes en estado PAGADO.
     */
    private DailySalesReportData collectReportData(LocalDate fecha) {
        OrderStatusEnum estado = OrderStatusEnum.PAGADO;

        BigDecimal ventasTotales = reportProviderPort
                .sumTotalByDateAndStatus(fecha, estado)
                .orElse(BigDecimal.ZERO);

        List<Object[]> rawWaiters = reportProviderPort.findSalesByWaiterAndDate(fecha, estado);
        List<MeseroSalesData> ventasPorMesero = rawWaiters.stream()
                .map(row -> new MeseroSalesData(
                        row[0] + " " + row[1],
                        (BigDecimal) row[2]))
                .toList();

        String platoMasVendido  = reportProviderPort.findMostSoldDishByDate(fecha, estado).orElse(null);
        String platoMenosVendido = reportProviderPort.findLeastSoldDishByDate(fecha, estado).orElse(null);

        return new DailySalesReportData(ventasTotales, ventasPorMesero, platoMasVendido, platoMenosVendido);
    }

    /**
          * @param titulo          Título principal del documento.
     * @param fecha           Fecha sobre la que se genera el informe.
     * @param nombreGenerador Nombre completo del usuario que genera el informe.
     * @return Bytes del PDF generado.
     */
    private byte[] buildPdf(DailySalesReportData data, String titulo, LocalDate fecha, String nombreGenerador) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(40, 50, 40, 50);

            PdfFont bold   = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD,
                    PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
            PdfFont normal = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA,
                    PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);

            // ── Logo del Restaurante ───────────────────────────────────────────
            byte[] imageBytes = null;
            try (var is = getClass().getResourceAsStream("/logo.png")) {
                if (is != null) {
                    imageBytes = is.readAllBytes();
                }
            } catch (Exception e) {
                log.error("Error al cargar el logo para el PDF: {}", e.getMessage());
            }

            if (imageBytes != null) {
                try {
                    com.itextpdf.io.image.ImageData imageData = com.itextpdf.io.image.ImageDataFactory.create(imageBytes);
                    com.itextpdf.layout.element.Image logoImage = new com.itextpdf.layout.element.Image(imageData);
                    logoImage.setWidth(70);
                    logoImage.setHeight(70);
                    logoImage.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    logoImage.setMarginBottom(8);
                    document.add(logoImage);
                } catch (Exception ex) {
                    log.error("Error al renderizar el logo en el PDF: {}", ex.getMessage());
                }
            }

            // ── Nombre del Restaurante ─────────────────────────────────────────
            Paragraph restaurantName = new Paragraph("RESTAURANTE GRUPO MORADO")
                    .setFont(bold)
                    .setFontSize(10)
                    .setFontColor(COLOR_DARK)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2);
            document.add(restaurantName);

            // ── Título principal ───────────────────────────────────────────────
            Paragraph tituloParagraph = new Paragraph(titulo)
                    .setFont(bold)
                    .setFontSize(18)
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(4);
            document.add(tituloParagraph);

            // ── Línea decorativa ──────────────────────────────────────────────
            Table lineaDecorativa = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);
            Cell lineaCell = new Cell()
                    .setHeight(2)
                    .setBackgroundColor(COLOR_PRIMARY)
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            lineaDecorativa.addCell(lineaCell);
            document.add(lineaDecorativa);

            // ── Subtítulo: fecha y generador ──────────────────────────────────
            String fechaFormateada = fecha.format(DATE_FORMATTER);
            Paragraph subtitulo = new Paragraph()
                    .add(new com.itextpdf.layout.element.Text("Fecha: ").setFont(bold).setFontColor(COLOR_DARK))
                    .add(new com.itextpdf.layout.element.Text(fechaFormateada).setFont(normal).setFontColor(COLOR_MUTED))
                    .add(new com.itextpdf.layout.element.Text("      Generado por: ").setFont(bold).setFontColor(COLOR_DARK))
                    .add(new com.itextpdf.layout.element.Text(nombreGenerador).setFont(normal).setFontColor(COLOR_MUTED))
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(subtitulo);

            // ── Tabla Resumen ─────────────────────────────────────────────────
            document.add(buildSectionTitle("Resumen General", bold));

            Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            addHeaderRow(resumenTable, bold, "Concepto", "Valor");
            BigDecimal total = data.ventasTotales() != null ? data.ventasTotales() : BigDecimal.ZERO;
            addDataRow(resumenTable, normal, "Ventas totales del día",
                    "$ " + total.toPlainString(), false);

            document.add(resumenTable);

            // ── Tabla Ventas por Mesero ───────────────────────────────────────
            document.add(buildSectionTitle("Ventas por Mesero", bold));

            Table meseroTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            addHeaderRow(meseroTable, bold, "Mesero", "Total");

            List<MeseroSalesData> meseros = data.ventasPorMesero();
            if (meseros.isEmpty()) {
                addDataRow(meseroTable, normal, "Sin ventas de meseros", "$ 0", false);
            } else {
                for (int i = 0; i < meseros.size(); i++) {
                    MeseroSalesData m = meseros.get(i);
                    addDataRow(meseroTable, normal,
                            m.nombreMesero(),
                            "$ " + m.totalVentas().toPlainString(),
                            i % 2 != 0);
                }
            }
            document.add(meseroTable);

            // ── Tabla Destacados ──────────────────────────────────────────────
            document.add(buildSectionTitle("Platos Destacados", bold));

            Table destacadosTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth()
                    .setMarginBottom(15);

            addHeaderRow(destacadosTable, bold, "Categoría", "Plato");
            addDataRow(destacadosTable, normal,
                    "Plato más vendido",
                    data.platoMasVendido() != null ? data.platoMasVendido() : "—", false);
            addDataRow(destacadosTable, normal,
                    "Plato menos vendido",
                    data.platoMenosVendido() != null ? data.platoMenosVendido() : "—", true);

            document.add(destacadosTable);

            // ── Pie de página ─────────────────────────────────────────────────
            Paragraph pie = new Paragraph(
                    "Documento generado automáticamente por el Sistema de Facturación e Inventario")
                    .setFont(normal)
                    .setFontSize(8)
                    .setFontColor(COLOR_MUTED)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(25);
            document.add(pie);

        } catch (IOException e) {
            log.error("Error al generar el PDF del informe: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo generar el PDF del informe.", e);
        }

        return baos.toByteArray();
    }

    // ─── Helpers de construcción de tablas ─────────────────────────────────────

    private Paragraph buildSectionTitle(String text, PdfFont bold) {
        return new Paragraph(text)
                .setFont(bold)
                .setFontSize(11)
                .setFontColor(COLOR_PRIMARY)
                .setMarginBottom(4)
                .setMarginTop(12);
    }

    private void addHeaderRow(Table table, PdfFont bold, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(
                    new Cell()
                            .add(new Paragraph(header).setFont(bold).setFontColor(ColorConstants.WHITE).setFontSize(9))
                            .setBackgroundColor(COLOR_DARK)
                            .setPadding(6)
                            .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            );
        }
    }

    private void addDataRow(Table table, PdfFont font, String col1, String col2, boolean alternate) {
        DeviceRgb rowBg = alternate ? COLOR_LIGHT : null;

        Cell c1 = new Cell()
                .add(new Paragraph(col1).setFont(font).setFontSize(9).setFontColor(COLOR_DARK))
                .setPadding(6)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        Cell c2 = new Cell()
                .add(new Paragraph(col2).setFont(font).setFontSize(9).setFontColor(COLOR_DARK))
                .setPadding(6)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

        if (rowBg != null) {
            c1.setBackgroundColor(rowBg);
            c2.setBackgroundColor(rowBg);
        } else {
            c1.setBackgroundColor(ColorConstants.WHITE);
            c2.setBackgroundColor(ColorConstants.WHITE);
        }

        table.addCell(c1);
        table.addCell(c2);
    }
}
