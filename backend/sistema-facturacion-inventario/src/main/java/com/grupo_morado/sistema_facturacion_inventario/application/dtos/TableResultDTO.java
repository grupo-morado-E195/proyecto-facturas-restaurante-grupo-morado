package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;

/**
 * DTO de respuesta para operaciones del módulo de Gestión de Mesas.
 * Expone únicamente los campos necesarios para el cliente.
 *
 * @param id           Identificador único de la mesa.
 * @param number       Número de mesa (visible para el personal).
 * @param disponibility Estado de disponibilidad: DISPONIBLE u OCUPADA.
 * @param status       Estado de registro: ACTIVO o INACTIVO.
 */
public record TableResultDTO(
        Long id,
        Integer number,
        DisponibilityStateEnum disponibility,
        StatusEnum status
) {}
