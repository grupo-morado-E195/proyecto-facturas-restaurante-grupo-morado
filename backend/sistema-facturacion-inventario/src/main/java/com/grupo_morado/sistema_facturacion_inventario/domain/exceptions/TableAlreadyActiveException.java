package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar reactivar una mesa que ya está ACTIVA.
 * Resulta en HTTP 409 Conflict.
 */
public class TableAlreadyActiveException extends RuntimeException {
    public TableAlreadyActiveException(String message) {
        super(message);
    }
}
