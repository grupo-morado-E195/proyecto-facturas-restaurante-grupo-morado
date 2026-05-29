package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar reactivar un menú que ya está activo.
 * Resulta en HTTP 409 Conflict.
 */
public class MenuAlreadyActiveException extends RuntimeException {
    public MenuAlreadyActiveException(String message) {
        super(message);
    }
}
