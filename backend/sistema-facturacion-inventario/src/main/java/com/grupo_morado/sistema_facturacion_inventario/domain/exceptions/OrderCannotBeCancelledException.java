package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar cancelar una orden cuyo estado no es PENDIENTE.
 * Resulta en HTTP 409 Conflict.
 */
public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String message) {
        super(message);
    }
}
