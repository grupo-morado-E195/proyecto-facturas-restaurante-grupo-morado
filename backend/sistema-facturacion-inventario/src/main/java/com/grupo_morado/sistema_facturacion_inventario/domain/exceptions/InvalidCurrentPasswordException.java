package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando la contraseña actual proporcionada
 * por el usuario no coincide con la contraseña registrada en base de datos.
 * Resulta en HTTP 401 Unauthorized.
 */
public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
