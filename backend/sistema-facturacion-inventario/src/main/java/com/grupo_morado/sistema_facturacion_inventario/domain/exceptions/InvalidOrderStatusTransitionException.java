package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar realizar una transición de estado de orden no permitida.
 * Resulta en HTTP 409 Conflict.
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
