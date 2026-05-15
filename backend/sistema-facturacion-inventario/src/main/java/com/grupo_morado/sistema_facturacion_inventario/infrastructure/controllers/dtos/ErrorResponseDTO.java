package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}
