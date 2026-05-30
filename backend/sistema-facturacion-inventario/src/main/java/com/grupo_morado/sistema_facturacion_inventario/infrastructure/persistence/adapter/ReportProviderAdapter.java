package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.ReportProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.OrderReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para el módulo de Informes.
 * Implementa ReportProviderPort delegando en OrderReportRepository.
 */
@Component
@RequiredArgsConstructor
public class ReportProviderAdapter implements ReportProviderPort {

    private final OrderReportRepository orderReportRepository;

    private Timestamp getStartTimestamp(LocalDate date) {
        return Timestamp.valueOf(date.atStartOfDay());
    }

    private Timestamp getEndTimestamp(LocalDate date) {
        return Timestamp.valueOf(date.atTime(23, 59, 59, 999999999));
    }

    @Override
    public Optional<BigDecimal> sumTotalByDateAndStatus(LocalDate date, OrderStatusEnum status) {
        return orderReportRepository.sumTotalByDateRangeAndStatus(getStartTimestamp(date), getEndTimestamp(date), status);
    }

    @Override
    public List<Object[]> findSalesByWaiterAndDate(LocalDate date, OrderStatusEnum status) {
        return orderReportRepository.findSalesByWaiterAndDateRange(getStartTimestamp(date), getEndTimestamp(date), status);
    }

    @Override
    public Optional<String> findMostSoldDishByDate(LocalDate date, OrderStatusEnum status) {
        return orderReportRepository.findMostSoldDishByDateRange(getStartTimestamp(date), getEndTimestamp(date), status);
    }

    @Override
    public Optional<String> findLeastSoldDishByDate(LocalDate date, OrderStatusEnum status) {
        return orderReportRepository.findLeastSoldDishByDateRange(getStartTimestamp(date), getEndTimestamp(date), status);
    }

    @Override
    public boolean existsByDateAndStatus(LocalDate date, OrderStatusEnum status) {
        return orderReportRepository.existsByDateRangeAndStatus(getStartTimestamp(date), getEndTimestamp(date), status);
    }
}
