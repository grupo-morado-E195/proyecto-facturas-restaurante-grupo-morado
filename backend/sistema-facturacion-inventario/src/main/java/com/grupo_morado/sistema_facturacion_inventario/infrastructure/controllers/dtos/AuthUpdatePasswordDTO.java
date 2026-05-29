package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthUpdatePasswordDTO(

        @NotBlank(message = "La nueva contraseña es obligatoria")
        String newPassword

) {}
