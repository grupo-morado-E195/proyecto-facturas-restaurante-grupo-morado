package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;

/**
 * DTO de respuesta para operaciones del módulo de Gestión de Menús.
 * Expone los campos necesarios del menú.
 *
 * @param id     Identificador único del menú.
 * @param name   Nombre del menú.
 * @param status Estado de registro: ACTIVO o INACTIVO.
 */
public record MenuResultDTO(
        Long id,
        String name,
        StatusEnum status
) {}
