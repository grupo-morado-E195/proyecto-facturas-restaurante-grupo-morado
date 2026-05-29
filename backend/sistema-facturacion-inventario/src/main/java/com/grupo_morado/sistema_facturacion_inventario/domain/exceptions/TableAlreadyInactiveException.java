package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar desactivar una mesa que ya está INACTIVA.
 * Resulta en HTTP 409 Conflict.
 */
public class TableAlreadyInactiveException extends RuntimeException {
    public TableAlreadyInactiveException(String message) {
        super(message);
    }
}
