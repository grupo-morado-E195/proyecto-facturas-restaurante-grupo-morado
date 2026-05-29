package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint de actualización de contraseña.
 * Se usa en dos flujos:
 * <ul>
 *   <li>Después de iniciar sesión con contraseña temporal (requiresPasswordChange = true).</li>
 *   <li>Cuando el usuario autenticado desea cambiar su contraseña desde su panel.</li>
 * </ul>
 * El correo del usuario NO se recibe en el body; se extrae del token JWT para garantizar
 * que el usuario solo pueda modificar su propia contraseña.
 *
 * @param newPassword Nueva contraseña definitiva elegida por el usuario.
 */
public record AuthUpdatePasswordDTO(

        @NotBlank(message = "La nueva contraseña es obligatoria")
        String newPassword

) {}
