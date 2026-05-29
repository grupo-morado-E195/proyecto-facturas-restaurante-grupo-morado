package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando el campo de confirmación de contraseña
 * no coincide exactamente con el campo de nueva contraseña.
 * Resulta en HTTP 400 Bad Request.
 */
public class PasswordConfirmationMismatchException extends RuntimeException {
    public PasswordConfirmationMismatchException(String message) {
        super(message);
    }
}
