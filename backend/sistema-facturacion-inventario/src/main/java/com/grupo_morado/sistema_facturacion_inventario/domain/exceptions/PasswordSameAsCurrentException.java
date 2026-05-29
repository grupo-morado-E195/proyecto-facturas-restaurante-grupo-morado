package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando la nueva contraseña elegida
 * es idéntica a la contraseña actual del usuario.
 * Resulta en HTTP 400 Bad Request.
 */
public class PasswordSameAsCurrentException extends RuntimeException {
    public PasswordSameAsCurrentException(String message) {
        super(message);
    }
}
