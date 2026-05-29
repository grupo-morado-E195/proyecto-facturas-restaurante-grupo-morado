package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar desactivar un plato que ya está inactivo.
 * Resulta en HTTP 409 Conflict.
 */
public class DishAlreadyInactiveException extends RuntimeException {
    public DishAlreadyInactiveException(String message) {
        super(message);
    }
}
