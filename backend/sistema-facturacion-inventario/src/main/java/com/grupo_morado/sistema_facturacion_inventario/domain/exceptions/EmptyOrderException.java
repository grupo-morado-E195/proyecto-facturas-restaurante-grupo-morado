package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar registrar una orden sin ningún detalle de plato.
 * Resulta en HTTP 409 Conflict.
 */
public class EmptyOrderException extends RuntimeException {
    public EmptyOrderException(String message) {
        super(message);
    }
}
