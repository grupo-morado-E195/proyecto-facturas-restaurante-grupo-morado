package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import java.math.BigDecimal;

/**
 * DTO de respuesta para operaciones del módulo de Gestión de Platos.
 * Expone los campos necesarios del plato.
 *
 * @param id          Identificador único del plato.
 * @param name        Nombre del plato.
 * @param description Descripción del plato.
 * @param price       Precio del plato.
 * @param stock       Stock del plato.
 * @param status      Estado de registro: ACTIVO o INACTIVO o PAUSADO.
 * @param menuId      Identificador único del menú al que pertenece.
 */
public record DishResultDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        StatusEnum status,
        Long menuId
) {}
