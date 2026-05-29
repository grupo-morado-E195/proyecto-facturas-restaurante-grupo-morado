package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar modificar una orden que no está en estado PENDIENTE.
 * Resulta en HTTP 409 Conflict.
 */
public class OrderCannotBeModifiedException extends RuntimeException {
    public OrderCannotBeModifiedException(String message) {
        super(message);
    }
}
