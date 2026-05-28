package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;

public record AuthRegisterResultDTO(
        String name,
        String lastname,
        String email,
        String role,
        StatusEnum status
) {}
