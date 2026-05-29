package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar desactivar una mesa que está en estado OCUPADA.
 * Una mesa ocupada no puede desactivarse porque tiene órdenes activas asociadas.
 * Resulta en HTTP 409 Conflict.
 */
public class TableOccupiedException extends RuntimeException {
    public TableOccupiedException(String message) {
        super(message);
    }
}
