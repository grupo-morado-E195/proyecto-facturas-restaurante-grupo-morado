package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;

/**
 * DTO de salida para el módulo de Gestión de Usuarios.
 * Expone únicamente los campos necesarios para la vista según el backlog (SFR-008).
 */
public record UserResultDTO(
        Long id,
        String name,
        String lastname,
        String email,
        String role,
        StatusEnum status
) {}
