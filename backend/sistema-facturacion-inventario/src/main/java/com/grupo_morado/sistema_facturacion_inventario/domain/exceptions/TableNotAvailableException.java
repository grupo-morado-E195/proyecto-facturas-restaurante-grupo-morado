package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar utilizar una mesa que no está disponible o no está activa.
 * Resulta en HTTP 409 Conflict.
 */
public class TableNotAvailableException extends RuntimeException {
    public TableNotAvailableException(String message) {
        super(message);
    }
}
